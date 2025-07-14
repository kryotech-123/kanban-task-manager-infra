environment = "dev"
# bucket_name = "kanban-task-manager-dev"
bucket_name = "terraform-state-kanban-dev"
tags = {
  Environment        = "dev"
  Owner              = "dev-team@kanban-taskmanager.com"
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
kms_key_arn      = "arn:aws:kms:eu-west-1:682033471539:key/mrk-104cb66de96d45e5a308e3928ef9d459"

db_name        = "postgres"
db_user        = "kanban_user"
db_password    = "Admin1234!"
ecr_repository = "realamponsah/lampstackphp"
