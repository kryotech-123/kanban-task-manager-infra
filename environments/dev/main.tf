# =================================================== FRONTEND INFRANSTRUCTURE ==================================================

#========================== BACKEND CONFIGURATION ==========================
# This configuration uses an S3 bucket for storing the Terraform state file.
# Ensure the bucket is pre-created and accessible.
terraform {
  
  backend "s3" {
    bucket         = "kanban-task-manager-terraform-state-files"  # Pre-created S3 bucket
    key            = "env:/dev/frontend/terraform.tfstate"
    region         = "eu-west-1"
    dynamodb_table = "terraform-lock-table-dev"  # DynamoDB table for state locking
    encrypt        = true
  }

}

# gateway  ---->  alb -- >  ecs
# gatea/login
# gateway/register

#========================== PROVIDER CONFIGURATION ==========================
# This configuration uses the AWS provider to manage resources in the specified region.
  provider "aws" {
    region = var.region
  }



#========================== MODULES ==========================
# The following modules are used to create the frontend infrastructure, including WAF, S3 bucket, and CloudFront distribution.
# Each module is configured with necessary variables such as application name, tags, and other specific settings.


module "frontend_waf" {
  source          = "../../modules/frontend/waf"
  application_name = var.application_name
  tags            = var.tags
}


module "random_id" {
  source = "../../modules/frontend/random_id"
}

module "frontend_s3" {
  source                                = "../../modules/frontend/s3"
  bucket_name                           = "${var.bucket_name}-${module.random_id.random_id}"
  cloudfront_origin_access_identity_iam_arn = module.frontend_cloudfront.origin_access_identity_iam_arn
  force_destroy                         = true
  tags                                  = var.tags
}



module "frontend_cloudfront" {
  source                     = "../../modules/frontend/cloudfront"
  application_name           = var.application_name
  s3_bucket_regional_domain_name = module.frontend_s3.bucket_regional_domain_name
  origin_id                 = "${var.application_name}-frontend"
  web_acl_arn               = module.frontend_waf.web_acl_arn
  tags                      = var.tags 
}