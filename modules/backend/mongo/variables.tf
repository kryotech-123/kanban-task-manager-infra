variable "cluster_name" {
  description = "Name of the DocumentDB cluster"
  type        = string
}



variable "instance_class" {
  description = "Instance type for DocumentDB instances"
  type        = string
  default     = "db.t3.medium"
}

variable "instance_count" {
  description = "Number of instances in the cluster (minimum 1, maximum 15)"
  type        = number
  default     = 1
}

variable "mongo_master_username" {
  description = "Master username for DocumentDB"
  type        = string
}

variable "mongo_master_password" {
  description = "Master password for DocumentDB"
  type        = string
  sensitive   = true
}

variable "backup_retention_period" {
  description = "Days to retain backups (1-35)"
  type        = number
  default     = 7
}

variable "preferred_backup_window" {
  description = "Preferred backup window in UTC"
  type        = string
  default     = "07:00-09:00"
}

variable "skip_final_snapshot" {
  description = "Whether to skip final snapshot on deletion"
  type        = bool
  default     = true
}

variable "vpc_id" {
  description = "VPC ID where DocumentDB will be deployed"
  type        = string
}

variable "subnet_ids" {
  description = "List of subnet IDs for DocumentDB subnet group"
  type        = list(string)
}

variable "mongo_security_group_id" {
  description = "List of security group IDs allowed to access DocumentDB"
  type        = string
}



variable "apply_immediately" {
  description = "Whether to apply changes immediately"
  type        = bool
  default     = false
}

variable "deletion_protection" {
  description = "Enable deletion protection"
  type        = bool
  default     = false
}

variable "enable_performance_insights" {
  description = "Enable Performance Insights"
  type        = bool
  default     = false
}

variable "storage_encrypted" {
  description = "Enable storage encryption"
  type        = bool
  default     = true
}

variable "kms_key_id" {
  description = "KMS key ARN for encryption (if not specified, AWS uses default key)"
  type        = string
  default     = null
}