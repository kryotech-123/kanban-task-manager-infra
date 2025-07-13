output "db_instance_endpoint" {
  description = "The connection endpoint"
  value       = aws_db_instance.this.endpoint
}

output "db_instance_arn" {
  description = "The ARN of the DB instance"
  value       = aws_db_instance.this.arn
}

output "db_instance_name" {
  description = "The database name"
  value       = aws_db_instance.this.db_name
}

output "db_instance_username" {
  description = "The master username"
  value       = aws_db_instance.this.username
  sensitive   = true
}

output "db_subnet_group_name" {
  description = "The name of the DB subnet group"
  value       = var.database_subnet_group_name
}