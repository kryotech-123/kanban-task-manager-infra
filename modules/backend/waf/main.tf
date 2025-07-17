# This file contains the configuration for the AWS WAF (Web Application Firewall)
# It sets up a WAF for the api gateway  to protect against common web exploits and ensure traffic is coming from CloudFront
# The WAF is configured with managed rule groups and a rate limit rule

# Create a WAF web ACL for the API Gateway
# This web ACL will be associated with the API Gateway to protect it from web attacks
# This web ACL will be associated with the CloudFront distribution to protect it from web attacks
resource "aws_wafv2_web_acl" "main" {
  name        = "${var.name_prefix}-waf"
  description = "WAF for API Gateway with OWASP Top 10 protections"
  scope       = "REGIONAL"

  default_action {
    allow {}
  }

# Managed rule groups for common web exploits
  rule {
    name     = "AWS-AWSManagedRulesCommonRuleSet"
    priority = 10

    override_action {
      count {}
    }

    statement { 
      managed_rule_group_statement {
        name        = "AWSManagedRulesCommonRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesCommonRuleSet"
      sampled_requests_enabled  = true
    }
  }

# Managed rule groups for known bad inputs
  rule {
    name     = "AWS-AWSManagedRulesKnownBadInputsRuleSet"
    priority = 20

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesKnownBadInputsRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesKnownBadInputsRuleSet"
      sampled_requests_enabled  = true
    }
  }

# SQL Injection rule group
  rule {
    name     = "AWS-AWSManagedRulesSQLiRuleSet"
    priority = 30

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesSQLiRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesSQLiRuleSet"
      sampled_requests_enabled  = true
    }
  }

# Managed rule groups for Linux and Unix systems
  # These rules are designed to protect against common vulnerabilities in Linux and Unix systems
  rule {
    name     = "AWS-AWSManagedRulesLinuxRuleSet"
    priority = 40

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesLinuxRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesLinuxRuleSet"
      sampled_requests_enabled  = true
    }
  }

  rule {
    name     = "AWS-AWSManagedRulesUnixRuleSet"
    priority = 50

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesUnixRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesUnixRuleSet"
      sampled_requests_enabled  = true
    }
  }

# Managed rule groups for bot control
  # These rules are designed to protect against malicious bots and automated traffic
  rule {
    name     = "AWS-AWSManagedRulesBotControlRuleSet"
    priority = 60

    override_action {
      count {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesBotControlRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "AWSManagedRulesBotControlRuleSet"
      sampled_requests_enabled  = true
    }
  }



# Rate limiting rule
  # This rule limits the number of requests from a single IP address to prevent abuse
  # The limit is set to 1000 requests per 5 minutes
  rule {
    name     = "RateLimit"
    priority = 80

    action {
      block {}
    }

    statement {
      rate_based_statement {
        limit              = var.rate_limit
        aggregate_key_type = "IP"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name               = "RateLimit"
      sampled_requests_enabled  = true
    }
  }



# Verify CloudFront Origin Rule
  # This rule checks if the request is coming from CloudFront by verifying the presence of a specific header
  # If the header is not present, the request is blocked
  rule {
        name     = "Verify-CloudFront-Origin"
        priority = 5  # Higher priority than other rules

        action {
            block {}
        }

        statement {
            not_statement {
            statement {
                byte_match_statement {
                field_to_match {
                    single_header {
                    name = "x-amz-cf-id"
                    }
                }
                positional_constraint = "EXACTLY"
                search_string         = "CloudFront" 
                text_transformation {
                    priority = 0
                    type     = "NONE"
                }
                }
            }
            }
        }

        visibility_config {
            cloudwatch_metrics_enabled = true
            metric_name               = "VerifyCloudFrontOrigin"
            sampled_requests_enabled  = true
        }
        }


  visibility_config {
    cloudwatch_metrics_enabled = true
    metric_name               = "${var.name_prefix}-waf-metrics"
    sampled_requests_enabled  = true
  }
  tags = merge(
      var.tags,
      {
        Name = "${var.name_prefix}-WAF"
      }
    )
}



resource "aws_wafv2_web_acl_association" "api_gateway" {
  resource_arn = var.resource_arn
  web_acl_arn  = aws_wafv2_web_acl.main.arn
}