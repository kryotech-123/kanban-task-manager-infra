variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
}

variable "resource_arns" {
  description = "Map of resource ARNs to monitor"
  type = object({
    api_gateway    = optional(string)
    ecs_cluster   = optional(string)
    rds_instance   = optional(string)
    nlb           = optional(string)
    cloudfront    = optional(string)
    nlb_target_group_blue = optional(string)
    nlb_target_group_green = optional(string)
    ecr_repository = optional(string)
    vpc            = optional(string)
    ecs_service    = optional(string)
  })
  default = null
}

variable "alarm_notification_emails" {
  description = "List of email addresses to notify for alarms"
  type        = list(string)
  default     = []
}

variable "alarm_sns_topic_kms_key" {
  description = "KMS key ID to encrypt the SNS topic"
  type        = string
  default     = null
}

variable "log_retention_days" {
  description = "Number of days to retain logs in CloudWatch"
  type        = number
  default     = 30
}

variable "enable_config_recorder" {
  description = "Whether to enable AWS Config recorder"
  type        = bool
  default     = false
}

variable "enable_xray" {
  description = "Whether to enable AWS X-Ray"
  type        = bool
  default     = false
}

variable "enable_cloudtrail" {
  description = "Whether to enable AWS CloudTrail"
  type        = bool
  default     = false
}

variable "aws_region" {
  description = "AWS region"
  type        = string
}


variable "tags" {
  description = "Dev environment tags"

  type        = map(string)
  default     = {}
}