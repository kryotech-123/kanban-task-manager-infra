# This file contains the variables for the ECS module used by the backend application
# It defines the necessary variables for configuring the ECS cluster, service, and task definitions
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
  default     = 8080
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
  default     = 1
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
variable "region" {
  description = "AWS region for the resources"
  type        = string
}



variable "mongo_user" {
  description = "MongoDB username for the ECS tasks"
  type        = string
}

variable "mongo_pass" {
  description = "Master password for MongoDB"
  type        = string  
  sensitive = true

}


variable "mongo_host" {
  description = "MongoDB host for the ECS tasks"
  type        = string
  
}

variable "mongo_name" {
  description = "MongoDB name for the ECS tasks"
  type        = string

  
}

variable "jwt_secret" {
  description = "JWT secret for the application"
  type        = string
}

variable "jwt_expire" {
  description = "JWT access token expiration time in seconds"
  type        = string
}

variable "jwt_refresh" {
  description = "JWT refresh token expiration time in seconds"
  type        = string
  
}


variable "email_host" {
  description = "Email host for sending notifications"
  type        = string  
  
}
variable "email_port" {
  description = "Email port for sending notifications"
  type        = string
}
variable "email_username" {
  description = "Email username for sending notifications"
  type        = string
} 
variable "email_password" {
  description = "Email password for sending notifications"
  type        = string
  sensitive   = true
}

variable "mongo_db_port" {
  description = "value for the MongoDB port for the ECS tasks"
  type = string
  default = "27017"
}

variable "email_ssl_trust" {
  description = "value for the email SSL trust for the ECS tasks"
  type = string
}

variable "sender_email" {
  description = "Sender email address for notifications"
  type        = string
  
}