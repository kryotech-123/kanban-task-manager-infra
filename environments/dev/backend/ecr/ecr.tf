variable "repository_name" {
  description = "Name of the ECR repository"
  type        = string
}
variable "kms_key_arn" {
  description = "ARN of the KMS key for encryption"
  type        = string
}

module "ecr_repository" {
  source = "../../../../modules/backend/ecr"
  repository_name = var.repository_name
  kms_key_arn     = var.kms_key_arn 
  
}