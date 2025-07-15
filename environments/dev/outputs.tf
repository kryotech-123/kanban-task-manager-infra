output "distribution_domain_name" {
  description = "Domain name of the CloudFront distribution"
  value       = module.frontend_cloudfront.distribution_domain_name

}

output "distribution_domain_id" {
  description = "value of the CloudFront distribution domain ID"
  value       = module.frontend_cloudfront.distribution_id
}

output "dev_bucket_name" {
  description = "name of bucket in dev environment"
  value       = module.frontend_s3.bucket_name
}

output "dashboard_url" {
  description = "URL of the CloudWatch dashboard"
  value       = module.monitoring.dashboard_url
  
}

output "load_balancer_arn" {
  description = "value of the ALB ARN"
  value       = module.ecs_cluster.load_balancer_arn
}