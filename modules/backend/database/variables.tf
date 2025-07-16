variable "name_prefix" {
  description = "Prefix for all resource names"
  type        = string
}

variable "is_replica" {
  description = "Whether this is a read replica instance"
  type        = bool
  default     = false
}

variable "subnet_ids" {
  description = "List of subnet IDs for the DB subnet group"
  type        = list(string)
}

variable "security_group_ids" {
  description = "List of security group IDs to associate"
  type        = list(string)
}



variable "allocated_storage" {
  description = "Initial storage allocation in GB"
  type        = number
  default     = 20
}



variable "instance_class" {
  description = "RDS instance type"
  type        = string
  default     = "db.t3.medium"
}

variable "db_name" {
  description = "Name of the initial database (only for primary)"
  type        = string
}

variable "database_subnet_group_name" {
  description = "Name of the database subnet group"
  type        = string
  
}
variable "db_username" {
  description = "Master username (only for primary)"
  type        = string
  default     = "postgres"
}

variable "db_password" {
  description = "Master password (only for primary)"
  type        = string
  sensitive   = true
}

variable "kms_key_arn" {
  description = "ARN of KMS key for encryption"
  type        = string
}

variable "parameter_group_name" {
  description = "Name of DB parameter group"
  type        = string
  default     = null
}

variable "skip_final_snapshot" {
  description = "Skip final snapshot when destroying"
  type        = bool
  default     = false
}

variable "backup_retention_period" {
  description = "Days to retain backups (0-35)"
  type        = number
  default     = 7
}

variable "backup_window" {
  description = "Preferred backup window"
  type        = string
  default     = "03:00-06:00"
}

variable "maintenance_window" {
  description = "Preferred maintenance window"
  type        = string
  default     = "Mon:00:00-Mon:03:00"
}

variable "storage_encrypted" {
  description = "Enable storage encryption"
  type        = bool
  default     = true
}

variable "replicate_source_db" {
  description = "Source DB ARN for replica"
  type        = string
  default     = null
}

variable "tags" {
  description = "Additional tags for resources"
  type        = map(string)
  default     = {}
}