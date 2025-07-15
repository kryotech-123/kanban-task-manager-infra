# SNS Topic for Alarms (unchanged)
resource "aws_sns_topic" "alarms" {
  name              = "${var.environment}-alarms-topic"
  kms_master_key_id = var.alarm_sns_topic_kms_key
  tags = {
    Environment = var.environment
  }
}

# SNS Subscription (unchanged)
resource "aws_sns_topic_subscription" "alarm_notifications" {
  for_each  = toset(var.alarm_notification_emails)
  topic_arn = aws_sns_topic.alarms.arn
  protocol  = "email"
  endpoint  = each.value
}

# API Gateway Alarms (unchanged)
resource "aws_cloudwatch_metric_alarm" "api_gateway_5xx_errors" {
  count               = try(var.resource_arns.api_gateway, null) != null ? 1 : 0
  alarm_name          = "${var.environment}-api-gateway-5xx-errors"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "5XXError"
  namespace           = "AWS/ApiGateway"
  period              = "300"
  statistic           = "Sum"
  threshold           = "10"
  alarm_description   = "API Gateway 5XX errors exceeded threshold"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  dimensions = {
    ApiName = var.resource_arns.api_gateway
  }
}

# RDS Alarms (unchanged)
resource "aws_cloudwatch_metric_alarm" "rds_cpu_high" {
  count               = try(var.resource_arns.rds_instance, null) != null ? 1 : 0
  alarm_name          = "${var.environment}-rds-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "3"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/RDS"
  period              = "300"
  statistic           = "Average"
  threshold           = "80"
  alarm_description   = "RDS CPU utilization high"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  dimensions = {
    DBInstanceIdentifier = var.resource_arns.rds_instance
  }
}

resource "aws_cloudwatch_metric_alarm" "rds_free_storage_low" {
  count               = try(var.resource_arns.rds_instance, null) != null ? 1 : 0
  alarm_name          = "${var.environment}-rds-storage-low"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "FreeStorageSpace"
  namespace           = "AWS/RDS"
  period              = "300"
  statistic           = "Average"
  threshold           = "10737418240" # 10GB
  alarm_description   = "RDS free storage space low"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  dimensions = {
    DBInstanceIdentifier = var.resource_arns.rds_instance
  }
}

# NLB Alarms (replacing ALB alarms)
resource "aws_cloudwatch_metric_alarm" "nlb_unhealthy_hosts" {
  count               = 1
  alarm_name          = "${var.environment}-nlb-unhealthy-hosts"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "3"
  metric_name         = "HealthyHostCount"
  namespace           = "AWS/NetworkELB"
  period              = "300"
  statistic           = "Minimum"
  threshold           = "1" # Alert if healthy hosts drop below 1
  alarm_description   = "NLB has no healthy hosts in target group"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  
  dimensions = {
    LoadBalancer =  var.resource_arns.nlb
    TargetGroup  =  var.resource_arns.nlb_target_group_blue
  }
}

resource "aws_cloudwatch_metric_alarm" "nlb_high_tcp_resets" {
  count               = try(var.resource_arns.nlb, null) != null ? 1 : 0
  alarm_name          = "${var.environment}-nlb-high-tcp-resets"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "TCP_ELB_Reset_Count"
  namespace           = "AWS/NetworkELB"
  period              = "300"
  statistic           = "Sum"
  threshold           = "10" # Alert if more than 10 resets in 5 minutes
  alarm_description   = "NLB TCP reset count high"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  dimensions = {
    LoadBalancer =  var.resource_arns.nlb
  }
}

resource "aws_cloudwatch_metric_alarm" "nlb_high_target_resets" {
  count               = try(var.resource_arns.nlb, null) != null ? 1 : 0
  alarm_name          = "${var.environment}-nlb-high-target-resets"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "TCP_Target_Reset_Count"
  namespace           = "AWS/NetworkELB"
  period              = "300"
  statistic           = "Sum"
  threshold           = "10" # Alert if more than 10 target resets in 5 minutes
  alarm_description   = "NLB target TCP reset count high"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  dimensions = {
    LoadBalancer =  var.resource_arns.nlb
  }
}

# CloudFront Alarms (unchanged)
resource "aws_cloudwatch_metric_alarm" "cloudfront_5xx_errors" {
  count               = try(var.resource_arns.cloudfront, null) != null ? 1 : 0
  alarm_name          = "${var.environment}-cloudfront-5xx-errors"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "1"
  metric_name         = "5xxErrorRate"
  namespace           = "AWS/CloudFront"
  period              = "300"
  statistic           = "Average"
  threshold           = "1" # 1% error rate
  alarm_description   = "CloudFront 5XX error rate high"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  dimensions = {
    DistributionId = var.resource_arns.cloudfront
    Region         = "Global"
  }
}

resource "aws_cloudwatch_metric_alarm" "cloudfront_cache_hit_rate" {
  count               = try(var.resource_arns.cloudfront, null) != null ? 1 : 0
  alarm_name          = "${var.environment}-cloudfront-low-cache-hit"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "3"
  metric_name         = "CacheHitRate"
  namespace           = "AWS/CloudFront"
  period              = "300"
  statistic           = "Average"
  threshold           = "90" # 90% cache hit rate
  alarm_description   = "CloudFront cache hit rate low"
  alarm_actions       = [aws_sns_topic.alarms.arn]
  dimensions = {
    DistributionId = var.resource_arns.cloudfront
    Region         = "Global"
  }
}