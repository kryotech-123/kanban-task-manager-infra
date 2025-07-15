


resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "${var.environment}-overview"
  dashboard_body = jsonencode({
    widgets = [
      # API Gateway Widget (unchanged)
      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 12
        height = 6
        properties = {
          metrics = [
            ["AWS/ApiGateway", "5XXError", "ApiName", var.resource_arns.api_gateway],
            [".", "4XXError", ".", "."],
            [".", "Count", ".", "."],
            [".", "Latency", ".", "."]
          ]
          period = 300
          stat   = "Sum"
          region = var.aws_region
          title  = "API Gateway Metrics"
          view   = "timeSeries"
        }
      },
      
      # ECS Widget (unchanged)
      {
        type   = "metric"
        x      = 12
        y      = 0
        width  = 12
        height = 6
        properties = {
          metrics = [
            ["AWS/ECS", "CPUUtilization", "ServiceName", var.resource_arns.ecs_service,"ClusterName", var.resource_arns.ecs_cluster],
            [".", "MemoryUtilization", ".", ".", ".", "."],
            ["AWS/ECS/ContainerInsights", "RunningTaskCount", "ClusterName",var.resource_arns.ecs_cluster]
          ]
          period = 300
          stat   = "Average"
          region = var.aws_region
          title  = "ECS Cluster Metrics"
          view   = "timeSeries"
        }
      },
      
      # RDS Widget (unchanged)
      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 12
        height = 6
        properties = {
          metrics = [
            ["AWS/RDS", "CPUUtilization", "DBInstanceIdentifier", try(var.resource_arns.rds_instance, "N/A")],
            [".", "FreeStorageSpace", ".", "."],
            [".", "DatabaseConnections", ".", "."],
            [".", "ReadLatency", ".", "."],
            [".", "WriteLatency", ".", "."]
          ]
          period = 300
          stat   = "Average"
          region = var.aws_region
          title  = "RDS Metrics"
          view   = "timeSeries"
          yAxis = {
            left = {
              min = 0
              max = 100
            }
          }
        }
      },
      
      # NLB Widget (updated for Network Load Balancer)
   {
    type   = "metric"
    x      = 12
    y      = 6
    width  = 12
    height = 6
    properties = {
        metrics = [
        ["AWS/NetworkELB", "ActiveFlowCount", "LoadBalancer", var.resource_arns.nlb],
        ["AWS/NetworkELB", "NewFlowCount", "LoadBalancer", var.resource_arns.nlb],
        ["AWS/NetworkELB", "ProcessedBytes", "LoadBalancer",  var.resource_arns.nlb],
        ["AWS/NetworkELB", "HealthyHostCount", "LoadBalancer",  var.resource_arns.nlb, "TargetGroup", try(var.resource_arns.nlb_target_group, "N/A")]
        ]
        period = 300
        stat   = "Sum"
        region = var.aws_region
        title  = "NLB Metrics"
        view   = "timeSeries"
    }
    },
      
      # NLB Target Health Widget (new)
      {
        type   = "metric"
        x      = 0
        y      = 12
        width  = 12
        height = 6
        properties = {
          metrics = [
                ["AWS/NetworkELB", "HealthyHostCount", "LoadBalancer", var.resource_arns.nlb, "TargetGroup", try(var.resource_arns.nlb_target_group, "N/A")],
                [".", "UnHealthyHostCount", ".", ".", ".", "."],
                [".", "TCP_Target_Reset_Count", ".", ".", ".", "."],
                [".", "TargetResponseTime", ".", ".", ".", "."]
                ]

          period = 300
          stat   = "Average"
          region = var.aws_region
          title  = "NLB Target Health"
          view   = "timeSeries"
        }
      },
      
      # CloudFront Widget (unchanged)
      {
        type   = "metric"
        x      = 12
        y      = 12
        width  = 12
        height = 6
        properties = {
          metrics = [
            ["AWS/CloudFront", "Requests", "DistributionId", var.resource_arns.cloudfront, "Region", "Global", { "region": "us-east-1" } ],
            [".", "4xxErrorRate", ".", ".", ".", "."],
            [".", "5xxErrorRate", ".", ".", ".", "."],
            [".", "CacheHitRate", ".", ".", ".", "."]
          ]
          period = 300
          stat   = "Average"
          region = "us-east-1"
          title  = "CloudFront Metrics"
          view   = "timeSeries"
          yAxis = {
            left = {
              min = 0
              max = 100
            }
          }
        }
      },
      
      # Status Text Widget (unchanged)
      {
        type   = "text"
        x      = 0
        y      = 18
        width  = 24
        height = 3
        properties = {
          markdown = "# ${upper(var.environment)} Environment Status\nLast updated: ${timestamp()}"
        }
      },
      
      # Alarm Status Widget (updated with NLB alarms)
      {
        type   = "alarm"
        x      = 0
        y      = 21
        width  = 24
        height = 6
        properties = {
          title  = "Alarm Status"
          alarms = [
            for alarm in [
              try(aws_cloudwatch_metric_alarm.api_gateway_5xx_errors[0].arn, ""),
              try(aws_cloudwatch_metric_alarm.rds_cpu_high[0].arn, ""),
              try(aws_cloudwatch_metric_alarm.nlb_unhealthy_hosts[0].arn, ""),
              try(aws_cloudwatch_metric_alarm.nlb_high_tcp_resets[0].arn, ""),
              try(aws_cloudwatch_metric_alarm.cloudfront_5xx_errors[0].arn, "")
            ] : alarm if alarm != ""
          ]
        }
      }
    ]
  })
}