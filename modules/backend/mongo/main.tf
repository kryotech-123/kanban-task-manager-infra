resource "aws_docdb_subnet_group" "default" {
  name       = "${var.cluster_name}-subnet-group"
  subnet_ids = var.subnet_ids

  tags = {
    Name = "${var.cluster_name}-subnet-group"
  }
}


resource "aws_docdb_cluster" "default" {
  cluster_identifier      = var.cluster_name
  engine                  = "docdb"
  master_username         = var.mongo_master_username
  master_password         = var.mongo_master_password
  backup_retention_period = var.backup_retention_period
  preferred_backup_window = var.preferred_backup_window
  skip_final_snapshot     = var.skip_final_snapshot
  deletion_protection     = var.deletion_protection
  storage_encrypted       = var.storage_encrypted
  kms_key_id             = var.kms_key_id
  apply_immediately       = var.apply_immediately
  db_subnet_group_name    = aws_docdb_subnet_group.default.name
  vpc_security_group_ids  = [var.mongo_security_group_id]

  lifecycle {
    ignore_changes = [master_password]
  }
}

resource "aws_docdb_cluster_instance" "default" {
  count              = var.instance_count
  identifier         = "${var.cluster_name}-instance-${count.index}"
  cluster_identifier = aws_docdb_cluster.default.id
  instance_class     = var.instance_class
  apply_immediately  = var.apply_immediately

  # Performance Insights
  enable_performance_insights = var.enable_performance_insights
}