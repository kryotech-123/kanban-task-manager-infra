bucket = "kanban-task-manager-terraform-state-files"
key            = "env:/dev/frontend/terraform.tfstate"
dynamodb_table = "terraform-lock-table-dev"
region = "eu-west-1"