variable "vpc_name" {
  description = "name of vpc"
  default     = "kanban_vpc"
  type        = string
}

variable "vpc_azs" {
  type        = list(string)
  description = "The availability zones in which resources will be deployed"
  default     = ["eu-west-1a", "eu-west-1b"]
}

variable "vpc_cidr" {
  type        = string
  default     = "10.0.0.0/16"
  description = "The network cidr of the vpc"
}

variable "private_subnets" {
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
  description = "Cidr definition for private subnets"
}

variable "public_subnets" {
  type        = list(string)
  default     = ["10.0.101.0/24", "10.0.102.0/24"]
  description = "Cidr definition for public subnets"
}


variable "database_subnets" {
  type        = list(string)
  default     = ["10.0.201.0/24", "10.0.202.0/24"]
  description = "Cidr defininition for database subnets"
}

