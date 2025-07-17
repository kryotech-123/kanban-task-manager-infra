# This file contains the configuration for the AWS VPC (Virtual Private Cloud)
# It sets up a VPC for the backend application with public and private subnets, NAT gateways, and security groups
# The VPC is configured to allow communication between the backend services and the database

# This module uses the Terraform AWS VPC module to create the necessary infrastructure
module "kanban_vpc" {
  source = "terraform-aws-modules/vpc/aws"

  name                         = var.vpc_name
  cidr                         = var.vpc_cidr
  azs                          = var.vpc_azs
  private_subnets              = var.private_subnets
  public_subnets               = var.public_subnets
  database_subnets             = var.database_subnets
  create_database_subnet_group = true
  enable_nat_gateway           = true
  single_nat_gateway           = true
  tags = merge(
      var.tags,
      {
        Name = "${var.vpc_name}-VPC"
      }
    )
}



# This file contains the security group configurations for the backend application
# It defines security groups for the database and MongoDB, allowing specific inbound and outbound traffic
# The security groups are associated with the VPC created in the networking module
resource "aws_security_group" "database_security_group" {
  name        = "${var.application_name}-db-sg"
  description = "Security group for the database"
  vpc_id      = module.kanban_vpc.vpc_id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
    description = "Allow inbound traffic from private subnets"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  
}
}

resource "aws_security_group" "mongo_security_group" {
  name        = "${var.application_name}-mongo-db-sg"
  description = "Security group for the mongo database"
  vpc_id      = module.kanban_vpc.vpc_id

  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
    description = "Allow inbound traffic from private subnets"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  
}
}