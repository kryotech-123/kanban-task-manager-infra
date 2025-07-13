variable "vpc_endpoint_id" {
  description = "VPC endpoint ID for API Gateway"
  type        = string
  
}
variable "load_balancer_arn" {
  description = "ARN of the load balancer for API Gateway"
  type        = string  
  
}

variable "load_balancer_dns" {
  description = "DNS of the load balancer for API Gateway"
  type        = string
  
}

variable "stage_name" {
  description = "Stage name for the API Gateway"
  type        = string
  
}
variable "api_name" {
  description = "Name of the API Gateway"
  type        = string
  
}

module "api_gateway" {
    source = "../../../../modules/backend/api_gateway"
    load_balancer_arn = var.load_balancer_arn
    load_balancer_dns = var.load_balancer_dns
    stage_name = var.stage_name
    api_name = var.api_name
    vpc_endpoint_id = var.vpc_endpoint_id
  
}