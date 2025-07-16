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
}

resource "aws_vpc_endpoint" "apigw_endpoint" {
  vpc_id              = module.kanban_vpc.vpc_id
  service_name        = "com.amazonaws.${var.region}.execute-api"  # e.g., "com.amazonaws.us-east-1.execute-api"
  vpc_endpoint_type   = "Interface"
  private_dns_enabled = true  
  subnet_ids          = module.kanban_vpc.private_subnets

  security_group_ids = [aws_security_group.apigw_sg.id]
}

resource "aws_security_group" "apigw_sg" {
  vpc_id = module.kanban_vpc.vpc_id
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/16"]  # Allow only from VPC
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

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