variable "database_subnet_group_name" {
  description = "Name of the database subnet group"
  type        = string
  
}

variable "subnet_ids" {
  description = "List of subnet IDs for the DB subnet group"
  type        = list(string)    
  
}

variable "name_prefix" {
  description = "Prefix for resource names"
  type        = string
  
}

variable "security_group_ids" {
  description = "List of security group IDs to associate with the database"
  type        = list(string)
  
}

variable "db_password" {
  description = "Master password for the database"
  type        = string
  sensitive   = true 
}

variable "db_username" {
  description = "Master username for the database"
  type        = string
  
}

variable "kms_key_arn" {
  description = "ARN of the KMS key for encryption"
  type        = string
  
}
module "database" {
    source = "../../../../modules/backend/database"
    db_username             = var.db_username
    db_password             = var.db_password
    database_subnet_group_name = var.database_subnet_group_name
    name_prefix = var.name_prefix
    security_group_ids = var.security_group_ids 
    subnet_ids = var.subnet_ids
    kms_key_arn = var.kms_key_arn
  
}