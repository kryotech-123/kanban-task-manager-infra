output "cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.cluster.name
}

output "alb_dns_name" {
  description = "ALB DNS name"
  value       = aws_lb.private.dns_name
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

output "target_group_arn" {
  description = "ALB target group ARN"
  value       = aws_lb_target_group.ecs.name
}