output "api_id" {
  description = "The ID of the API Gateway"
  value       = aws_api_gateway_rest_api.main.id
}
output "api_arn" {
  description = "Full ARN of the API Gateway stage"
  value       = "arn:aws:apigateway:${var.region}::/restapis/${aws_api_gateway_rest_api.main.id}/stages/${aws_api_gateway_stage.main.stage_name}"
}
output "api_name" {
  description = "Name of the API Gateway"
  value       = aws_api_gateway_rest_api.main.name
  
}
output "api_endpoint" {
  description = "The endpoint of the API Gateway"
  # value       = "${aws_api_gateway_rest_api.main.id}.execute-api.${data.aws_region.current.name}.amazonaws.com"
  value = "${aws_api_gateway_rest_api.main.id}.execute-api.${data.aws_region.current.region}.amazonaws.com"

}

output "vpc_link_id" {
  description = "The ID of the VPC link"
  value       = aws_api_gateway_vpc_link.main.id
}

output "stage_name" {
  description = "The name of the deployed stage"
  value       = var.stage_name
}

data "aws_region" "current" {}