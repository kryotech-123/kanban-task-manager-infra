output "vpc_id" {
  value       = module.kanban_vpc.vpc_id
  description = "vpc id of kanban vpc"
}

output "private_subnets" {
  value       = module.kanban_vpc.private_subnets
  description = "list of private subnets id"
}

output "database_subnets" {
  value       = module.kanban_vpc.database_subnets
  description = "list of database subnets id"
}

output "database_subnet_group_name" {
  description = "Name of the database subnet group"
  value       = module.kanban_vpc.database_subnet_group_name
}
output "database_security_group_id" {
  description = "ID of the database security group"
  value       = aws_security_group.database_security_group.id
  
}

output "vpc_endpoint" {
  description = "Type of the VPC endpoint for API Gateway"
  value       = aws_vpc_endpoint.apigw_endpoint.id
  
}