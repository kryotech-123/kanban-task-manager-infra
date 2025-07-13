environment = "dev"
bucket_name = "kanban-task-manager-dev"
tags = {
  Environment        = "dev"
  Owner              = "dev-team@yourcompany.com"
  CostCenter         = "12345"
  AutoShutdown       = "true"
  TicketReference    = "DEV-1234"
  DataClassification = "internal"
}
application_name = "kanban-task-manager-dev"
region           = "eu-west-1"
stage_name       = "dev"
vpc_azs          = ["eu-west-1a", "eu-west-1b"]
vpc_cidr         = "10.0.0.0/16"
private_subnets  = ["10.0.1.0/24", "10.0.2.0/24"]
public_subnets   = ["10.0.101.0/24", "10.0.102.0/24"]
database_subnets = ["10.0.201.0/24", "10.0.202.0/24"]
kms_key_arn      = "arn:aws:kms:eu-west-1:084375583047:key/mrk-48d7d0c178c3492dbfb2e345ea8f9166" 

db_name = "value"
db_username = "kanban_user" 
db_password = "Admin1234!" # Ensure this is stored securely and not hardcoded in production
ecr_repository = "kanban-task-manager-backend-repo"
api_gateway_arn = "arn:aws:apigateway:eu-west-1::restapis/your-api-id/stages/dev"