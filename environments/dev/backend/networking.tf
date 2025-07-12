module "kanban_vpc" {
  source = "../../../modules/backend/networking"

  vpc_name         = "kanban_vpc"
  vpc_azs          = ["eu-west-1a", "eu-west-1b"]
  vpc_cidr         = "10.0.0.0/16"
  private_subnets  = ["10.0.1.0/24", "10.0.2.0/24"]
  public_subnets   = ["10.0.101.0/24", "10.0.102.0/24"]
  database_subnets = ["10.0.201.0/24", "10.0.202.0/24"]
}


# outputs
output "vpc_id" {
  value = module.kanban_vpc.vpc_id
}
