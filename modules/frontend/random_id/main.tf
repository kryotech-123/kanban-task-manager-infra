
# This file contains the configuration for generating a random ID
# It is used to create unique resource names for the frontend S3 bucket
# The random ID is generated using the random_id resource from Terraform

resource "random_id" "bucket_suffix" {
  byte_length = 4
}

output "random_id" {
  description = "Random ID for unique resource naming"
  value       = random_id.bucket_suffix.hex
}