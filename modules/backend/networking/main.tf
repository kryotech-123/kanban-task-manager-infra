module "kanban_vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name                         = var.vpc_name
  cidr                         = var.vpc_cidr
  azs                          = var.vpc_azs
  private_subnets              = var.private_subnets
  public_subnets               = var.public_subnets
  database_subnets             = var.database_subnets
  create_database_subnet_group = true
}
