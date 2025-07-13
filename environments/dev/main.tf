# =================================================== FRONTEND INFRANSTRUCTURE ==================================================

#========================== BACKEND CONFIGURATION ==========================
# This configuration uses an S3 bucket for storing the Terraform state file.
# Ensure the bucket is pre-created and accessible.
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 6.0.0"
    }
  }
  backend "s3" {
    bucket         = "kanban-task-manager-terraform-state-files" # Pre-created S3 bucket
    key            = "env:/dev/frontend/terraform.tfstate"
    region         = "eu-west-1"
    dynamodb_table = "terraform-lock-table-dev" # DynamoDB table for state locking
    encrypt        = true
    profile        = "kanban"
  }

}


#========================== PROVIDER CONFIGURATION ==========================
# This configuration uses the AWS provider to manage resources in the specified region.
provider "aws" {
  region = var.region
  profile        = "kanban"

}


#========================== MODULES ==========================
# The following modules are used to create the frontend infrastructure, including WAF, S3 bucket, and CloudFront distribution.
# Each module is configured with necessary variables such as application name, tags, and other specific settings.


module "frontend_waf" {
  source           = "../../modules/frontend/waf"
  application_name = var.application_name
  tags             = var.tags
}


module "random_id" {
  source = "../../modules/frontend/random_id"
}

module "frontend_s3" {
  source                                    = "../../modules/frontend/s3"
  bucket_name                               = "${var.bucket_name}-${module.random_id.random_id}"
  cloudfront_origin_access_identity_iam_arn = module.frontend_cloudfront.origin_access_identity_iam_arn
  force_destroy                             = true
  tags                                      = var.tags
}



module "frontend_cloudfront" {
  source                         = "../../modules/frontend/cloudfront"
  application_name               = var.application_name
  s3_bucket_regional_domain_name = module.frontend_s3.bucket_regional_domain_name
  origin_id                      = "${var.application_name}-frontend"
  web_acl_arn                    = module.frontend_waf.web_acl_arn
  tags                           = var.tags
}



# ========================= BACKEND INFRASTRUCTURE =================================

module "backend_networking" {
  source           = "../../modules/backend/networking"
  vpc_name         = "${var.application_name}-vpc"
  application_name = var.application_name 
  vpc_azs          = var.vpc_azs
  vpc_cidr         = var.vpc_cidr
  private_subnets  = var.private_subnets
  public_subnets   = var.public_subnets
  database_subnets = var.database_subnets
}


module "ecs_cluster" {
  source   = "./backend/ecs"
  app_name = var.application_name

  vpc_cidr        = var.vpc_cidr
  vpc_id          = module.backend_networking.vpc_id
  private_subnets = module.backend_networking.private_subnets
  ecr_repository  = var.ecr_repository
}

module "waf" {
  source          = "./backend/waf"
  api_gateway_arn = var.api_gateway_arn
}

module "ecr_repository" {
  source          = "./backend/ecr"
  repository_name = "${var.application_name}-ecr-repo"
  kms_key_arn     = var.kms_key_arn
}

module "database" {
  source                  = "./backend/database"
  db_username             = var.db_username 
  db_password             = var.db_password
  database_subnet_group_name = module.backend_networking.database_subnet_group_name
  name_prefix = "${var.application_name}-db"
  security_group_ids = [module.backend_networking.database_security_group_id]
  subnet_ids = module.backend_networking.database_subnets 
  kms_key_arn = var.kms_key_arn 
  
}