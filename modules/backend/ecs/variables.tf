variable "vpc_id" {
  description = "VPC ID where resources will be created"
  type        = string
}

variable "private_subnets" {
  description = "List of private subnet IDs for ALB and Fargate"
  type        = list(string)
}

variable "app_name" {
  description = "Application name used for resource naming"
  type        = string
}

variable "container_port" {
  description = "Port exposed by the container"
  type        = number
  default     = 80
}

variable "ecr_repository" {
  description = "Container image to deploy"
  type        = string
}

variable "fargate_cpu" {
  description = "Fargate CPU units (1024 = 1 vCPU)"
  type        = number
  default     = 256
}
variable "db_host" {
  description = "Database host for the ECS tasks"
  type        = string
}

variable "db_password" {
  description = "value for the database password for the ECS tasks"
  type        = string
}

variable "db_user" {
  description = "value for the database username for the ECS tasks"
  type        = string  
  
}

variable "db_name" {
  description = "Database name for the ECS tasks"
  type        = string
}

variable "fargate_memory" {
  description = "Fargate memory (MB)"
  type        = number
  default     = 512
}

variable "desired_count" {
  description = "Number of tasks to run"
  type        = number
  default     = 2
}
variable "environment" {
  description = "Deployment environment (dev/stage/prod)"
  type        = string
  default     = "dev"
}
variable "vpc_cidr" {
  description = "VPC ID"
  type        = string
}

variable "tags" {
  description = "Common tags for all resources"
  type        = map(string)
  default     = {}
}
variable "internal_alb" {
  description = "Whether the ALB should be internal"
  type        = bool
  default     = true
}