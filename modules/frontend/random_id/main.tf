resource "random_id" "bucket_suffix" {
  byte_length = 4
}

output "random_id" {
  description = "Random ID for unique resource naming"
  value       = random_id.bucket_suffix.hex
}