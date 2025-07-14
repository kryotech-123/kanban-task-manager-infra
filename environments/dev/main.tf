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
    bucket         = "terraform-state-kanban-dev" 
    key            = "env:/dev/kanban-task-manager/terraform.tfstate"
    region         = "eu-west-1"
    dynamodb_table = "terraform-lock-table-dev" # DynamoDB table for state locking
    encrypt        = true
    profile        = "kanban"
  }

}


#========================== PROVIDER CONFIGURATION ==========================
# This configuration uses the AWS provider to manage resources in the specified region.
provider "aws" {
  region  = var.region
  profile = "kanban"

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
  region           = var.region
}


module "ecs_cluster" {
  source          = "../../modules/backend/ecs"
  app_name        = var.application_name
  db_host         = module.database.db_instance_endpoint
  db_password     = var.db_password
  db_name         = var.db_name
  vpc_cidr        = var.vpc_cidr
  vpc_id          = module.backend_networking.vpc_id
  private_subnets = module.backend_networking.private_subnets
  ecr_repository  = var.ecr_repository
  db_user         = var.db_user
}

module "waf" {
  source       = "../../modules/backend/waf"
  resource_arn = module.api_gateway.api_arn
  name_prefix  = var.application_name
}
module "ecr_repository" {
  source          = "./backend/ecr"
  repository_name = "${var.application_name}-ecr-repo"
  kms_key_arn     = var.kms_key_arn
}

module "database" {
  source                     = "../../modules/backend/database"
  db_username                = var.db_user
  db_password                = var.db_password
  database_subnet_group_name = module.backend_networking.database_subnet_group_name
  name_prefix                = var.application_name
  db_name                    = var.db_name
  security_group_ids         = [module.backend_networking.database_security_group_id]
  subnet_ids                 = module.backend_networking.database_subnets
  kms_key_arn                = var.kms_key_arn
}


module "api_gateway" {
  source            = "../../modules/backend/api_gateway"
  load_balancer_arn = module.ecs_cluster.load_balancer_arn
  load_balancer_dns = module.ecs_cluster.load_balancer_dns
  stage_name        = var.stage_name
  region            = var.region
  api_name          = "${var.application_name}-api"
  vpc_endpoint_id   = module.backend_networking.vpc_endpoint
  tags              = var.tags
}