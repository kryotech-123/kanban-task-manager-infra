variable "api_name" {
  description = "The name of the API Gateway"
  type        = string
}

variable "stage_name" {
  description = "The name of the API Gateway stage"
  type        = string
}

variable "vpc_endpoint_id" {
  description = "The ID of the VPC endpoint for private API Gateway integration"
  type        = string
}

variable "load_balancer_dns" {
  description = "The DNS name of the load balancer that routes to ECS"
  type        = string
}

variable "load_balancer_arn" {
  description = "The ARN of the load balancer for WAF association"
  type        = string
}

variable "allowed_origins" {
  description = "List of allowed origins for CORS"
  type        = list(string)
  default     = ["*"]
}
variable "region" {
  description = "AWS region where the API Gateway is deployed"
  type        = string
  
}

variable "cloudwatch_role_arn" {
  description = "ARN of the IAM role for CloudWatch logging"
  type        = string
  default     = null
  
}

variable "tags" {
  description = "Tags to apply to the API Gateway resources"
  type        = map(string)
  default     = {}
  
}

variable "allowed_methods" {
  description = "List of allowed HTTP methods for CORS"
  type        = list(string)
  default     = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
}

variable "allowed_headers" {
  description = "List of allowed headers for CORS"
  type        = list(string)
  default     = ["*"]
}

