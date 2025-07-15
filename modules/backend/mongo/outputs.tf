output "cluster_id" {
  description = "DocumentDB cluster identifier"
  value       = aws_docdb_cluster.default.id
}

output "cluster_endpoint" {
  description = "DocumentDB cluster endpoint"
  value       = aws_docdb_cluster.default.endpoint
}

output "cluster_reader_endpoint" {
  description = "DocumentDB cluster reader endpoint"
  value       = aws_docdb_cluster.default.reader_endpoint
}

output "cluster_port" {
  description = "DocumentDB cluster port"
  value       = aws_docdb_cluster.default.port
}

output "cluster_master_username" {
  description = "DocumentDB master username"
  value       = aws_docdb_cluster.default.master_username
  sensitive   = true
}

output "cluster_resource_id" {
  description = "DocumentDB cluster resource ID"
  value       = aws_docdb_cluster.default.cluster_resource_id
}

output "instance_endpoints" {
  description = "List of instance endpoints"
  value       = aws_docdb_cluster_instance.default[*].endpoint
}