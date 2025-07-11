variable "application_name" {
  description = "Name of the application"
  type        = string
}

variable "s3_bucket_regional_domain_name" {
  description = "Regional domain name of the S3 bucket"
  type        = string
}

variable "origin_id" {
  description = "Unique identifier for the origin"
  type        = string
}

variable "web_acl_arn" {
  description = "ARN of the WAF Web ACL to associate with CloudFront"
  type        = string
}



variable "comment" {
  description = "Comment for the distribution"
  type        = string
  default     = "Managed by Terraform"
}

variable "price_class" {
  description = "Price class for the distribution (PriceClass_100, PriceClass_200, PriceClass_All)"
  type        = string
  default     = "PriceClass_100"
}

variable "lambda_edge_arn" {
  description = "ARN of the Lambda@Edge function for request processing"
  type        = string
  default     = ""
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}