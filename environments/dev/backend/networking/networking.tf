#================ Variables =========================
variable "vpc_azs" {
  type        = list(string)
  description = "The availability zones in which resources will be deployed"
}
variable "vpc_name" {
  description = "Name of the application"
  type        = string
  
}
variable "vpc_cidr" {
  type        = string
  description = "The network cidr of the vpc"
}

variable "private_subnets" {
  type        = list(string)
  description = "Cidr definition for private subnets"
}
variable "public_subnets" {
  type        = list(string)
  description = "Cidr definition for public subnets"
}
variable "database_subnets" {
  type        = list(string)
  description = "Cidr defininition for database subnets"
}

variable "application_name" {
  description = "Name of the application"
  type        = string  
  
}

variable "region" {
  type        = string
  description = "AWS region where the resources will be deployed"
  
}
# ========================= MODULE FOR VPC =================================
# This module creates a VPC with the specified CIDR block and subnets.
module "kanban_vpc" {
  source = "../../../../modules/backend/networking"
  vpc_name         = "${var.vpc_name}-vpc"
  vpc_azs          = var.vpc_azs
  vpc_cidr         = var.vpc_cidr
  private_subnets  = var.private_subnets
  public_subnets   = var.public_subnets
  database_subnets = var.database_subnets
  application_name = var.application_name
  region = var.region
}



# ========================== OUTPUTS ==========================
# Outputs from the VPC module to be used in other parts of the infrastructure.

# outputs
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