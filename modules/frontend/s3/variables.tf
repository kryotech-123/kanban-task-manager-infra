variable "bucket_name" {
  description = "Name of the S3 bucket for frontend assets"
  type        = string
}

variable "force_destroy" {
  description = "Whether to allow force destroy of the bucket (useful for development)"
  type        = bool
  default     = false
}

variable "cloudfront_origin_access_identity_iam_arn" {
  description = "IAM ARN of the CloudFront Origin Access Identity"
  type        = string
}

variable "tags" {
  description = "Tags to apply to resources"
  type        = map(string)
  default     = {}
}