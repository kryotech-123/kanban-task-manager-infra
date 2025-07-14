# ================================= Frontend Bucket ===================================
variable "bucket_name" {
  description = "Name of frontend s3 bucket"
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


# =============================================== API GATEWAY =========================================================
variable "stage_name" {
  description = "Stage name for the API Gateway"
  type        = string
}

# ================================ KMS KEY =========================================================
variable "kms_key_arn" {
  description = "ARN of the KMS key for encryption"
  type        = string
}

# ================================== ECR ========================================================

variable "ecr_repository" {
  description = "ECR repository for the backend container image"
  type        = string

}





# ================================================== DATABASE =========================================================
variable "db_name" {
  description = "Name of the database"
  type        = string
}
variable "db_user" {
  description = "Master username for the database"
  type        = string
  default     = "postgres"
}
variable "db_password" {
  description = "Master password for the database"
  type        = string
  sensitive   = true
}


# ============================================ General config ========================================
variable "application_name" {
  description = "name of application"
}


variable "tags" {
  description = "Dev environment tags"
}

variable "environment" {
  description = "Environment name"
}
variable "region" {
  description = "AWS region for the resources"
  type        = string
}


