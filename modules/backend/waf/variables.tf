variable "name_prefix" {
  description = "Prefix for WAF resources"
  type        = string
}

variable "resource_arn" {
  description = "The ARN of the API Gateway to associate with WAF"
  type        = string
}



variable "blocked_paths" {
  description = "List of paths to explicitly block"
  type        = list(string)
  default     = []
}

variable "enable_owasp_top_10" {
  description = "Enable OWASP Top 10 protections"
  type        = bool
  default     = true
}

variable "enable_bot_protection" {
  description = "Enable bot protection rules"
  type        = bool
  default     = true
}

variable "enable_ip_reputation" {
  description = "Enable AWS managed IP reputation rules"
  type        = bool
  default     = true
}

variable "enable_rate_limit" {
  description = "Enable rate limiting"
  type        = bool
  default     = true
}

variable "rate_limit" {
  description = "Requests per 5 minute period per IP"
  type        = number
  default     = 2000
}