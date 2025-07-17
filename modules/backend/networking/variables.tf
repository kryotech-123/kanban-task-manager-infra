# This file contains the variables for the networking module
# It defines the necessary inputs for creating a VPC, subnets, and security groups for the backend application
# The variables include VPC name, CIDR block, availability zones, subnets, and tags
variable "vpc_name" {
  description = "name of vpc"
  default     = "kanban_vpc"
  type        = string
}

variable "vpc_azs" {
  type        = list(string)
  description = "The availability zones in which resources will be deployed"
}

variable "application_name" {
  description = "Name of the application"
  type        = string
  
}
variable "vpc_cidr" {
  type        = string
  description = "The network cidr of the vpc"
}

variable "private_subnets" {
  type        = list(string)
  description = "Cidr definition for private subnets"
}

variable "region" {
  type        = string
  description = "AWS region where the resources will be deployed"
  
}
variable "public_subnets" {
  type        = list(string)
  description = "Cidr definition for public subnets"
}


variable "database_subnets" {
  type        = list(string)
  description = "Cidr defininition for database subnets"
}


variable "tags" {
  type        = map(string)
  description = "Tags to be applied to all resources"
  default     = {}
  
}

