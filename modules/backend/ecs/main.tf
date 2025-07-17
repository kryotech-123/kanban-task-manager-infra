# This file contains the main configuration for the AWS ECS service with CodeDeploy integration
# It sets up an ECS service that uses CodeDeploy for blue/green deployments
# The service is configured to use a Network Load Balancer (NLB) for routing traffic



# Resource provisioning for an ECS cluster
# This resource creates an ECS cluster that will host the backend application
resource "aws_ecs_cluster" "cluster" {
  name = "${var.app_name}-cluster"
  tags = merge(
      var.tags,
      {
        Name = "${var.app_name}-backend-cluster"
      }
    )
}


# CodeDeploy application and deployment group for ECS
# This section sets up CodeDeploy to manage deployments for the ECS service
# It creates a CodeDeploy application and a deployment group that uses the ECS service
resource "aws_codedeploy_app" "ecs_app" {
  compute_platform = "ECS"
  name             = "${var.app_name}-codedeploy-app"
  tags = merge(
      var.tags,
      {
        Name = "${var.app_name}-codedeploy-deployment-controller"
      }
    )
}
resource "aws_codedeploy_deployment_group" "ecs_deployment_group" {
  app_name               = aws_codedeploy_app.ecs_app.name
  deployment_group_name  = "${var.app_name}-deployment-group"
  service_role_arn       = aws_iam_role.codedeploy_role.arn
  deployment_config_name = "CodeDeployDefault.ECSAllAtOnce" # Can also use ECSLinear10PercentEvery1Minute or ECSCanary10Percent5Minutes

  auto_rollback_configuration {
    enabled = true
    events  = ["DEPLOYMENT_FAILURE"]
  }

  blue_green_deployment_config {
    deployment_ready_option {
      action_on_timeout = "CONTINUE_DEPLOYMENT"
    }

    terminate_blue_instances_on_deployment_success {
      action                           = "TERMINATE"
      termination_wait_time_in_minutes = 5
    }
  }

  deployment_style {
    deployment_option = "WITH_TRAFFIC_CONTROL"
    deployment_type   = "BLUE_GREEN"
  }

  ecs_service {
    cluster_name = aws_ecs_cluster.cluster.name
    service_name = aws_ecs_service.app.name
  }

  load_balancer_info {
    target_group_pair_info {
      prod_traffic_route {
        listener_arns = [aws_lb_listener.tcp.arn]
      }

      target_group {
        name = aws_lb_target_group.ecs_blue.name
      }

      # Green target group for blue/green deployments
      target_group {
        name = aws_lb_target_group.ecs_green.name
      }
    }
  }
}


# Network Load Balancer
# This section creates a Network Load Balancer (NLB) for routing traffic to the ECS service
# The NLB is configured to handle TCP traffic and forward it to the ECS tasks
resource "aws_lb" "private" {
  name               = "${var.app_name}-nlb-${var.environment}"
  internal           = true
  load_balancer_type = "network"
  subnets            = var.private_subnets

  enable_deletion_protection = false

  tags = merge(
    var.tags,
    {
      "Terraform" = "true"
      "NLB-Type"  = "internal"
    }
  )
}

# Target Group for NLB
# This section creates a target group for the NLB that routes traffic to the ECS tasks
# The target group is configured for TCP traffic and uses the ECS tasks' IP addresses as targets
resource "aws_lb_target_group" "ecs_blue" {
  name_prefix = "tgb-"
  port        = var.container_port
  protocol    = "TCP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  deregistration_delay = 30
  
  # Health check for TCP (simpler than HTTP for NLB)
  health_check {
    enabled             = true
    interval            = 30
    protocol            = "TCP"
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  lifecycle {
    create_before_destroy = true
  }
}


# Target Group for NLB (Green)
# This section creates a second target group for the NLB that can be used for blue/green deployments
# The green target group is configured similarly to the blue target group
resource "aws_lb_target_group" "ecs_green" {
  name_prefix = "tgg-"
  port        = var.container_port
  protocol    = "TCP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  deregistration_delay = 30
  
  health_check {
    enabled             = true
    interval            = 30
    protocol            = "TCP"
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  lifecycle {
    create_before_destroy = true
  }
}


# TCP Listener
# This section creates a TCP listener for the NLB that forwards traffic to the ECS tasks
# The listener listens on port 80 and forwards traffic to the blue target group
# Since codedeploy handles the traffic routing, we use a single listener, it automatically switches between blue and green target groups
# that forwards traffic to the currently active target group
resource "aws_lb_listener" "tcp" {
  load_balancer_arn = aws_lb.private.arn
  port              = 80
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ecs_blue.arn
  }
}

# Security Groups for Fargate Tasks
# This section creates a security group for the Fargate tasks
# The security group allows inbound traffic from the NLB and outbound traffic to the internet
module "fargate_sg" {
  source  = "terraform-aws-modules/security-group/aws"

  name        = "${var.app_name}-fargate-sg"
  description = "Security group for Fargate tasks"
  vpc_id      = var.vpc_id

  # Allow traffic from NLB (NLB doesn't use security groups, so we allow from VPC CIDR)
  ingress_with_cidr_blocks = [
    {
      rule        = "http-8080-tcp"
      cidr_blocks = var.vpc_cidr
      description = "Allow TCP from VPC (NLB traffic)"
    }
  ]
  
  egress_rules = ["all-all"]
}

# Task Definition 
# This section defines the ECS task definition for the backend application
# The task definition specifies the Docker image, CPU and memory requirements, and environment variables
# It also configures logging to CloudWatch
resource "aws_ecs_task_definition" "app" {
  family                   = "${var.app_name}-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.fargate_cpu
  memory                   = var.fargate_memory
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([{
    name      = var.app_name
    image     = var.ecr_repository
    essential = true
    portMappings = [{
      containerPort = var.container_port
      hostPort      = var.container_port
    }]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs.name
        "awslogs-region"        = data.aws_region.current.id 
        "awslogs-stream-prefix" = "ecs"
      }
    }
    environment = [
       {
        name  = "DB_HOST"
        value = var.db_host
      },
      {
        name  = "DB_NAME"
        value = var.db_name
      },
      {
        name  = "DB_USER"
        value = var.db_user
      },
      {
        name  = "DB_PASS"
        value = var.db_password
      },
       {
        name  = "MONGODB_USER"
        value = var.mongo_user
      },
      {
        name  = "MONGODB_PASS"
        value = var.mongo_pass
      },
      {
        name  = "MONGODB_HOST"
        value = var.mongo_host
      },
      {
        name  = "MONGODB_NAME"
        value = var.mongo_name
      },
        
       {
        name  = "JWT_SECRET"
        value = var.jwt_secret
      },
      {
        name  = "JWT_ACCESS_EXPIRATION"
        value = var.jwt_expire
      },
      {
        name  = "JWT_REFRESH_EXPIRATION"
        value = var.jwt_refresh
      },
      {
        name  = "EMAIL_HOST"
        value = var.email_host
      },
       
       {
        name  = "EMAIL_PORT"
        value = var.email_port
      },
      {
        name  = "EMAIL_USERNAME"
        value = var.email_username
      },
      {
        name  = "EMAIL_PASSWORD"
        value = var.email_password
      },
       {
        name  = "MONGODB_PORT"
        value = var.mongo_db_port
      },
      {
        name = "EMAIL_SSL_TRUST"
        value = var.email_ssl_trust
      }
     

    ]
  }])
  
}

# ECS Service
# This section creates an ECS service that runs the task definition
# The service is configured to use the NLB and is set up for blue/green deployments
# It uses CodeDeploy to manage the deployment process
# The service is set to run in Fargate mode with the specified number of desired tasks
resource "aws_ecs_service" "app" {
  name            = "${var.app_name}-service"
  cluster         = aws_ecs_cluster.cluster.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  deployment_controller {
    type = "CODE_DEPLOY"
  }
  network_configuration {
    subnets          = var.private_subnets
    security_groups  = [module.fargate_sg.security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.ecs_blue.arn
    container_name   = var.app_name
    container_port   = var.container_port
  }

  depends_on = [aws_lb.private, aws_lb_listener.tcp]
   lifecycle {
    ignore_changes = [
      task_definition,
      load_balancer, 
      desired_count
    ]
  }
}

# Auto Scaling
# This section sets up auto scaling for the ECS service
# It configures the service to scale based on CPU utilization :target_tracking_scaling_policy_configuration:
resource "aws_appautoscaling_target" "ecs_target" {
  max_capacity       = 10
  min_capacity       = var.desired_count
  resource_id        = "service/${aws_ecs_cluster.cluster.name}/${aws_ecs_service.app.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

# Auto Scaling Policy
# This section creates a scaling policy for the ECS service
# The policy uses target tracking to maintain a CPU utilization target of 70%
resource "aws_appautoscaling_policy" "ecs_cpu_policy" {
  name               = "${var.app_name}-cpu-scaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs_target.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target.service_namespace

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
    target_value = 70  
  }
}

# IAM Roles
# This section creates IAM roles for the ECS tasks and the task execution role
# The roles are used to grant permissions for the tasks to access AWS services
# The task execution role is used for pulling images from ECR and writing logs to CloudWatch
# The task role is used for the application to access other AWS services
resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.app_name}-ecs-task-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action = "sts:AssumeRole",
      Effect = "Allow",
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
    }]
  })
}

# Attach the Amazon ECS task execution role policy to the task execution role
# This policy allows the ECS tasks to pull images from ECR and write logs to CloudWatch
resource "aws_iam_role" "ecs_task_role" {
  name = "${var.app_name}-ecs-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action = "sts:AssumeRole",
      Effect = "Allow",
      Principal = {
        Service = "ecs-tasks.amazonaws.com"
      }
    }]
  })
}

# Attach the Amazon ECS task execution role policy to the task execution role
# This policy allows the ECS tasks to pull images from ECR and write logs to CloudWatch
resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# CloudWatch Logs
# This section creates a CloudWatch log group for the ECS tasks
# The log group is used to store the logs generated by the ECS tasks
# The logs are configured to have a retention period of 7 days
resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/${var.app_name}-task"
  retention_in_days = 7
}



# IAM Role for CodeDeploy
# This section creates an IAM role for CodeDeploy to manage deployments for the ECS service
# The role allows CodeDeploy to access the ECS service and perform deployments
resource "aws_iam_role" "codedeploy_role" {
  name = "${var.app_name}-codedeploy-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "codedeploy.amazonaws.com"
        }
      }
    ]
  })
}

# Attach the AWS CodeDeploy role policy to the CodeDeploy role
# This policy allows CodeDeploy to manage deployments for the ECS service
resource "aws_iam_role_policy_attachment" "codedeploy_role_policy" {
  role       = aws_iam_role.codedeploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCodeDeployRoleForECS"
}

data "aws_region" "current" {}