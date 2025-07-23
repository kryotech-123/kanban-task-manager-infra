# ================================= Frontend Bucket ===================================
variable "bucket_name" {
  description = "Name of frontend s3 bucket"
  default     = "kanban-task-manager-dev"
  type        = string
}

# ========================= NETWORKING VARIABLES =================================
variable "vpc_azs" {
  type        = list(string)
  description = "The availability zones in which resources will be deployed"
  default     = ["eu-west-1a", "eu-west-1b"]
}
variable "vpc_cidr" {
  type        = string
  description = "The network cidr of the vpc"
  default     = "10.0.0.0/16"
}

variable "private_subnets" {
  type        = list(string)
  description = "Cidr definition for private subnets"
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}


variable "public_subnets" {
  type        = list(string)
  description = "Cidr definition for public subnets"
  default     = ["10.0.101.0/24", "10.0.102.0/24"]
}


variable "database_subnets" {
  type        = list(string)
  description = "Cidr defininition for database subnets"
  default     = ["10.0.201.0/24", "10.0.202.0/24"]
}


# =============================================== API GATEWAY =========================================================
variable "stage_name" {
  description = "Stage name for the API Gateway"
  type        = string
  default     = "dev"
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
  default     = "postgres"
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
  type        = string
  default     = "kanban-task-manager-dev"
}


variable "tags" {
  description = "Dev environment tags"
  default = {
    Environment        = "dev"
    Owner              = "dev-team@kanban-taskmanager.com"
    CostCenter         = "12345"
    AutoShutdown       = "true"
    TicketReference    = "DEV-1234"
    DataClassification = "internal"
  }
}

variable "environment" {
  description = "Environment name"
  default     = "dev"
}

variable "region" {
  description = "AWS region for the resources"
  type        = string
  default     = "eu-west-1"
}




# ================================== ECS Variables =========================================================


variable "mongo_user" {
  default = "kanban_mongo_user"
  type    = string
}

variable "mongo_pass" {
  description = "Master password for MongoDB"
  type        = string
  sensitive   = true
}

variable "mongo_host" {
  type        = string
  description = "Mongo host endpoint"
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
  default     = "15000000"
}

variable "jwt_refresh" {
  description = "JWT refresh token expiration time in seconds"
  type        = string
  default     = "70000000"
}


variable "email_host" {
  description = "Email host for sending notifications"
  type        = string
  default     = "smtp.gmail.com"
}

variable "email_port" {
  description = "Email port for sending notifications"
  type        = number
  default     = 587
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

variable "email_ssl_trust" {
  description = "email ssl trust for sending notifications"
  type        = string
  default     = "smtp.gmail.com"
}

variable "sender_email" {
  description = "Sender email address for notifications"
  type        = string
}

