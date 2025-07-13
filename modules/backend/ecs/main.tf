resource "aws_ecs_cluster" "cluster" {
  name = "${var.app_name}-cluster"
}

# NLB (Private)

# Network Load Balancer
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
resource "aws_lb_target_group" "ecs" {
  name_prefix = "tg-"
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

# TCP Listener (replaces HTTP listener)
resource "aws_lb_listener" "tcp" {
  load_balancer_arn = aws_lb.private.arn
  port              = var.container_port
  protocol          = "TCP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ecs.arn
  }
}

# Security Groups for Fargate Tasks
module "fargate_sg" {
  source  = "terraform-aws-modules/security-group/aws"

  name        = "${var.app_name}-fargate-sg"
  description = "Security group for Fargate tasks"
  vpc_id      = var.vpc_id

  # Allow traffic from NLB (NLB doesn't use security groups, so we allow from VPC CIDR)
  ingress_with_cidr_blocks = [
    {
      rule        = "http-80-tcp"
      cidr_blocks = var.vpc_cidr
      description = "Allow TCP from VPC (NLB traffic)"
    }
  ]
  
  egress_rules = ["all-all"]
}

# Task Definition (unchanged)
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
      }

    ]
  }])
  
}

# ECS Service (updated to use NLB)
resource "aws_ecs_service" "app" {
  name            = "${var.app_name}-service"
  cluster         = aws_ecs_cluster.cluster.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnets
    security_groups  = [module.fargate_sg.security_group_id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.ecs.arn
    container_name   = var.app_name
    container_port   = var.container_port
  }

  depends_on = [aws_lb.private, aws_lb_listener.tcp]
}

# Auto Scaling (unchanged)
resource "aws_appautoscaling_target" "ecs_target" {
  max_capacity       = 10
  min_capacity       = var.desired_count
  resource_id        = "service/${aws_ecs_cluster.cluster.name}/${aws_ecs_service.app.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

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

# IAM Roles (unchanged)
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

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# CloudWatch Logs (unchanged)
resource "aws_cloudwatch_log_group" "ecs" {
  name              = "/ecs/${var.app_name}-task"
  retention_in_days = 7
}

data "aws_region" "current" {}