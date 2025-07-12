variable "bucket_name" {
  description = "Name of frontend s3 bucket"
}

variable "tags" {
  description = "Dev environment tags"
}

variable "environment" {
  description = "Environment name"
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