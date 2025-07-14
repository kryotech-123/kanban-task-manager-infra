resource "aws_api_gateway_rest_api" "main" {
  name        = var.api_name
  description = "API Gateway for ECS backend"

  endpoint_configuration {
    types            = ["REGIONAL"]
  }
}

resource "aws_api_gateway_resource" "proxy" {
  rest_api_id = aws_api_gateway_rest_api.main.id
  parent_id   = aws_api_gateway_rest_api.main.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "proxy" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_resource.proxy.id
  http_method   = "ANY"
  authorization = "NONE"
    request_parameters = {
    "method.request.path.proxy" = true
  }
}

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

resource "aws_api_gateway_method" "proxy_root" {
  rest_api_id   = aws_api_gateway_rest_api.main.id
  resource_id   = aws_api_gateway_rest_api.main.root_resource_id
  http_method   = "ANY"
  authorization = "NONE"
}

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

resource "aws_api_gateway_vpc_link" "main" {
  name        = "${var.api_name}-vpc-link"
  target_arns = [var.load_balancer_arn]
}

resource "aws_api_gateway_deployment" "main" {
  depends_on = [
    aws_api_gateway_integration.proxy,
    aws_api_gateway_integration.proxy_root
  ]

  rest_api_id = aws_api_gateway_rest_api.main.id
}

resource "aws_api_gateway_stage" "main" {
  stage_name    = var.stage_name
  rest_api_id   = aws_api_gateway_rest_api.main.id
  deployment_id = aws_api_gateway_deployment.main.id

  # access_log_settings {
  #   destination_arn = aws_cloudwatch_log_group.api_gateway_logs.arn
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
  # depends_on = [aws_cloudwatch_log_group.api_gateway]
}

# CORS configuration
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

# resource "aws_cloudwatch_log_group" "api_gateway" {
#   name              = "/aws/apigateway/${var.api_name}-${var.stage_name}"
#   retention_in_days = 30  
  
#   tags = merge(
#     var.tags,
#     {
#       Name = "${var.api_name}-api-gateway-logs"
#     }
#   )
# }


# resource "aws_iam_role_policy" "api_gateway_logs" {
#   name = "api-gateway-cloudwatch-logs"
#   role = aws_iam_role.api_gateway.id

#   policy = jsonencode({
#     Version = "2012-10-17",
#     Statement = [{
#       Effect = "Allow",
#       Action = [
#         "logs:CreateLogGroup",
#         "logs:CreateLogStream",
#         "logs:DescribeLogGroups",
#         "logs:DescribeLogStreams",
#         "logs:PutLogEvents",
#         "logs:GetLogEvents",
#         "logs:FilterLogEvents"
#       ],
#       Resource = "*"
#     }]
#   })
# }

# # 1. Create IAM Role for API Gateway to assume
# resource "aws_iam_role" "api_gateway_logging" {
#   name               = "${var.api_name}-api-gateway-cloudwatch-role"
#   assume_role_policy = jsonencode({
#     Version = "2012-10-17",
#     Statement = [
#       {
#         Action = "sts:AssumeRole",
#         Effect = "Allow",
#         Principal = {
#           Service = "apigateway.amazonaws.com"
#         }
#       }
#     ]
#   })

#   tags = merge(
#     var.tags,
#     {
#       Name = "${var.api_name}-api-gateway-logs-role"
#     }
#   )
# }

# # 2. Attach the policy we created earlier
# resource "aws_iam_role_policy_attachment" "api_gateway_logs" {
#   role       = aws_iam_role.api_gateway_logging.name
#   policy_arn = aws_iam_role_policy.api_gateway_logs.arn
# }

# # 3. Add permissions for X-Ray if enabled
# resource "aws_iam_role_policy_attachment" "xray_write" {
#   role       = aws_iam_role.api_gateway_logging.name
#   policy_arn = "arn:aws:iam::aws:policy/AWSXrayWriteOnlyAccess"
# }