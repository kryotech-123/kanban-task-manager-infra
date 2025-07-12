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
