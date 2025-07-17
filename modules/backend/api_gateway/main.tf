# This file contains the Terraform configuration for the API Gateway
# It sets up an API Gateway to route requests to the ECS backend service
# The API Gateway is configured with a proxy resource to handle all HTTP methods

# Defining resource for the API Gateway
# This resource creates a REST API that will be used to route requests to the ECS backend service
resource "aws_api_gateway_rest_api" "main" {
  name        = var.api_name
  description = "API Gateway for ECS backend"

  endpoint_configuration {
    types            = ["REGIONAL"]
  }
  tags = merge(
      var.tags,
      {
        Name = "${var.api_name}-backend-api"
      }
    )
}


# Create a resource for the API Gateway
# This resource will be used to define the proxy path for the API Gateway
resource "aws_api_gateway_resource" "proxy" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_rest_api.main.root_resource_id
  path_part   = "{proxy+}"
}


# Create a method for the API Gateway proxy resource
# This method allows any HTTP method to be used with the proxy resource
resource "aws_api_gateway_method" "proxy" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.proxy.id
  http_method   = "ANY"
  authorization = "NONE"
    request_parameters = {
    "method.request.path.proxy" = true
  }
}

# Create an integration for the API Gateway proxy resource
# This integration routes requests to the ECS backend service using HTTP proxy integration
# The integration uses a VPC link to connect to the backend service
resource "aws_api_gateway_integration" "proxy" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.proxy.http_method

  type                    = "HTTP_PROXY"
  integration_http_method = "ANY"
  uri                     = "http://${var.load_balancer_dns}/{proxy}"

  connection_type = "VPC_LINK"
  connection_id   = aws_api_gateway_vpc_link.main.id
    request_parameters = {
    "integration.request.path.proxy" = "method.request.path.proxy"
  }

}


# Create a method for the root resource of the API Gateway
# This method allows any HTTP method to be used with the root resource
# It is configured to route requests to the same backend service as the proxy resource
resource "aws_api_gateway_method" "proxy_root" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_rest_api.main.root_resource_id
  http_method   = "ANY"
  authorization = "NONE"
}

# Create an integration for the root resource of the API Gateway
# This integration routes requests to the ECS backend service using HTTP proxy integration
# The integration uses a VPC link to connect to the backend service
# It allows the root path to be accessed directly without needing a specific resource
resource "aws_api_gateway_integration" "proxy_root" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  resource_id = aws_api_gateway_method.proxy_root.resource_id
  http_method = aws_api_gateway_method.proxy_root.http_method

  type                    = "HTTP_PROXY"
  integration_http_method = "ANY"
  uri                     = "http://${var.load_balancer_dns}/"

  connection_type = "VPC_LINK"
  connection_id   = aws_api_gateway_vpc_link.main.id
}

# Create a VPC link for the API Gateway
# This VPC link allows the API Gateway to connect to the network load balancer in a private VPC
resource "aws_api_gateway_vpc_link" "main" {
  name        = "${var.api_name}-vpc-link"
  target_arns = [var.load_balancer_arn]
}

# Create a deployment for the API Gateway
# This deployment is necessary to make the API Gateway changes effective
# It creates a new deployment whenever there are changes to the API Gateway configuration
resource "aws_api_gateway_deployment" "main" {
  depends_on = [
    aws_api_gateway_integration.proxy,
    aws_api_gateway_integration.proxy_root
  ]

  rest_api_id = aws_api_gateway_rest_api.main.id
}

# Create a stage for the API Gateway
# This stage is used to manage different versions of the API
# It allows for deployment of the API to a specific stage, such as "dev" or "prod"
# The stage can be used to access the API via a specific URL
resource "aws_api_gateway_stage" "main" {
  stage_name    = var.stage_name
  rest_api_id   = aws_api_gateway_rest_api.main.id
  deployment_id = aws_api_gateway_deployment.main.id

  # access_log_settings {
  #   destination_arn = var.cloudwatch_role_arn
  #   format = jsonencode({
  #     requestId       = "$context.requestId"
  #     ip              = "$context.identity.sourceIp"
  #     caller          = "$context.identity.caller"
  #     user            = "$context.identity.user"
  #     requestTime     = "$context.requestTime"
  #     httpMethod      = "$context.httpMethod"
  #     resourcePath    = "$context.resourcePath"
  #     status          = "$context.status"
  #     protocol        = "$context.protocol"
  #     responseLength  = "$context.responseLength"
  #   })
  # }

  xray_tracing_enabled = true
  # depends_on = [aws_api_gateway_account.main]
}


# CORS configuration
# This section enables CORS (Cross-Origin Resource Sharing) for the API Gateway
# It allows the API to be accessed from different origins, which is useful for frontend applications
module "cors" {
  source  = "squidfunk/api-gateway-enable-cors/aws"
  version = "0.3.3"

  api_id          = aws_api_gateway_rest_api.main.id
  api_resource_id = aws_api_gateway_resource.proxy.id

  allow_origin  = join(",", var.allowed_origins)
  allow_methods = var.allowed_methods             
  allow_headers = var.allowed_headers
}




# ========================= CLOUDWATCH LOGS =========================


# resource "aws_iam_role" "api_gateway_logging" {

#    name = "${var.api_name}-api-gateway-cloudwatch-role"
#   assume_role_policy = jsonencode({
#     Version = "2012-10-17",
#     Statement = [{
#       Action = "sts:AssumeRole",
#       Effect = "Allow",
#       Principal = {
#         Service = "apigateway.amazonaws.com"
#       }
#     }]
#   })
  
# }

# resource "aws_iam_role_policy_attachment" "api_gateway_logs" {
#   role       = aws_iam_role.api_gateway_logging.name
#   policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
# }

# resource "aws_api_gateway_account" "main" {
#   cloudwatch_role_arn = aws_iam_role.api_gateway_logging.arn
# }


# resource "aws_iam_role_policy_attachment" "xray_write" {
#   role       = aws_iam_role.api_gateway_logging.name
#   policy_arn = "arn:aws:iam::aws:policy/AWSXrayWriteOnlyAccess"
# }

