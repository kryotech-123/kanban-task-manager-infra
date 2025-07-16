environment = "dev"
bucket_name = "kanban-task-manager-dev"
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
ecr_repository = "682033471539.dkr.ecr.eu-west-1.amazonaws.com/kanban-task-manager-dev-ecr-repo:c29811bcb4e6a339e782ec2518dda7e1f6238053"
mongo_name = "kanban_mongo_db"
mongo_user = "kanban_mongo_user"
mongo_pass = "Admin1234!"
jwt_expire = "150000"
jwt_refresh = "7000"
jwt_secret = "pHBIznGIiVW1RwrCaSuyM9XL/dLcITxT6PQKcmkUPqQ="
email_host = "your.smtp.server.com"
email_port = 587
email_username = "your-email@domain.com"
email_password = "some_password"
email_ssl_trust = "value"
sender_email = "sender@gmail.com"






