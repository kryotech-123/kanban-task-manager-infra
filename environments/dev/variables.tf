variable "bucket_name" {
  description = "Name of frontend s3 bucket"
}

variable "tags" {
  description = "Dev environment tags"
}

variable "environment" {
  description = "Environment name"
}

# ========================= NETWORKING VARIABLES =================================
variable "vpc_azs" {
  type        = list(string)
  description = "The availability zones in which resources will be deployed"
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


variable "stage_name" {
  description = "Stage name for the API Gateway"
  type        = string
}

variable "application_name" {
  description = "name of application"
}
variable "region" {
  description = "AWS region for the resources"
  type        = string
}

variable "ecr_repository" {
  description = "ECR repository for the backend container image"
  type        = string
  
}