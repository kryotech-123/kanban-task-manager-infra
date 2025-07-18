# Outputs for ECS Module
# This file contains the outputs for the ECS module used by the backend application

output "cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.cluster.name
}

output "load_balancer_dns" {
  description = "ALB DNS name"
  value       = aws_lb.private.dns_name
}

output "load_balancer_arn" {
  description = "ALB ARN"
  value       = aws_lb.private.arn_suffix
  
}
output "load_balancer_arn_original" {
  description = "ALB ARN"
  value       = aws_lb.private.arn
  
}

output "cluster_id" {
  description = "ECS cluster ID"
  value       = aws_ecs_cluster.cluster.id
  
}
output "service_name" {
  description = "ECS service name"
  value       = aws_ecs_service.app.name
}

output "task_definition_arn" {
  description = "Task definition ARN"
  value       = aws_ecs_task_definition.app.arn
}

output "blue_target_group_arn" {
  description = "ALB target group ARN"
  value       = aws_lb_target_group.ecs_blue.name
}

output "green_target_group_arn" {
  description = "ALB target group ARN"
  value       = aws_lb_target_group.ecs_blue.name
}