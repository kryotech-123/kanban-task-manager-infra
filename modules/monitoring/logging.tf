# # Central Log Group
# resource "aws_cloudwatch_log_group" "central_logs" {
#   name              = "/aws/${var.environment}/central-logs"
#   retention_in_days = var.log_retention_days
#   tags = {
#     Environment = var.environment
#   }
# }

# # API Gateway Log Subscription
# resource "aws_cloudwatch_log_subscription_filter" "api_gateway" {
#   count           = try(var.resource_arns.api_gateway, null) != null ? 1 : 0
#   name            = "${var.environment}-api-gateway-logs"
#   log_group_name  = "/aws/apigateway/${var.resource_arns.api_gateway}"
#   filter_pattern  = ""
#   destination_arn = aws_cloudwatch_log_group.central_logs.arn
#   role_arn        = aws_iam_role.log_forwarder.arn
#   depends_on      = [aws_iam_role.log_forwarder]
# }

# resource "aws_cloudwatch_log_subscription_filter" "ecs" {
#   count           = try(var.resource_arns.ecs_cluster, null) != null ? 1 : 0
#   name            = "${var.environment}-ecs-logs"
#   log_group_name  = "/aws/ecs/${var.resource_arns.ecs_cluster}"
#   filter_pattern  = ""
#   destination_arn = aws_cloudwatch_log_group.central_logs.arn
#   role_arn        = aws_iam_role.log_forwarder.arn
#   depends_on      = [aws_iam_role.log_forwarder]
# }



# # NLB Access Logs Subscription
# resource "aws_cloudwatch_log_subscription_filter" "nlb_access" {
#   count           = try(var.resource_arns.nlb, null) != null ? 1 : 0
#   name            = "${var.environment}-nlb-access-logs"
#   log_group_name  = "/aws/elasticloadbalancing/${split("/", var.resource_arns.nlb)[3]}"
#   filter_pattern  = ""
#   destination_arn = aws_cloudwatch_log_group.central_logs.arn
#     role_arn        = aws_iam_role.log_forwarder.arn  

# }




# # ECR Scan Findings Log Subscription
# resource "aws_cloudwatch_log_subscription_filter" "ecr_scan" {
#   count           = try(var.resource_arns.ecr_repository, null) != null ? 1 : 0
#   name            = "${var.environment}-ecr-scan-logs"
#   log_group_name  = "/aws/ecr/repository/${var.resource_arns.ecr_repository}"
#   filter_pattern  = ""
#   destination_arn = aws_cloudwatch_log_group.central_logs.arn
#     role_arn        = aws_iam_role.log_forwarder.arn  

# }

# # VPC Flow Log Subscription
# resource "aws_cloudwatch_log_subscription_filter" "vpc_flow" {
#   count           = try(var.resource_arns.vpc, null) != null ? 1 : 0
#   name            = "${var.environment}-vpc-flow-logs"
#   log_group_name  = "/aws/vpc/${var.resource_arns.vpc}/flowlog"
#   filter_pattern  = ""
#   destination_arn = aws_cloudwatch_log_group.central_logs.arn
#     role_arn        = aws_iam_role.log_forwarder.arn  

# }


