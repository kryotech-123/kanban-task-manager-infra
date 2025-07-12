# Variables for ECS Module
variable "app_name" {
  description = "Application name used for resource naming"
  type        = string
  
}

variable "ecr_repository" {
  description = "Container image to deploy"
  type        = string  

}

variable "private_subnets" {
  description = "List of private subnet IDs for ALB and Fargate"
  type        = list(string)
  
}

variable "vpc_id" {
  description = "VPC ID where resources will be created"
  type        = string
  
}
variable "vpc_cidr" {
  description = "VPC ID where resources will be created"
  type        = string
  
}


# Module for ECS Cluster

module "ecs_cluster" {
  source  = "../../../../modules/backend/ecs"
  vpc_id = var.vpc_id
  private_subnets = var.private_subnets
  app_name = var.app_name
  ecr_repository = var.ecr_repository
  vpc_cidr = var.vpc_cidr
}