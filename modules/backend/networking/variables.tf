variable "vpc_name" {
  description = "name of vpc"
  default     = "kanban_vpc"
  type        = string
}

variable "vpc_azs" {
  type        = list(string)
  description = "The availability zones in which resources will be deployed"
}

variable "vpc_cidr" {
  type        = string
  description = "The network cidr of the vpc"
}

variable "private_subnets" {
  type        = list(string)
  description = "Cidr definition for private subnets"
}

variable "public_subnets" {
  type        = list(string)
  description = "Cidr definition for public subnets"
}


variable "database_subnets" {
  type        = list(string)
  description = "Cidr defininition for database subnets"
}

