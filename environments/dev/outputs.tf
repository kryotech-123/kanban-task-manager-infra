output "distribution_domain_name" {
  description = "Domain name of the CloudFront distribution"
  value       = module.frontend_cloudfront.distribution_domain_name

}

output "distribution_domain_id" {
  description = "value of the CloudFront distribution domain ID"
  value       = module.frontend_cloudfront.distribution_id
}