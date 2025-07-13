

resource "aws_db_instance" "this" {
  identifier             = var.name_prefix
  allocated_storage      = var.allocated_storage
  engine                 = "postgres"
  instance_class         = var.instance_class
  db_name                = var.is_replica ? null : var.db_name
  username               = var.is_replica ? null : var.db_username
  password               = var.is_replica ? null : var.db_password
  port                   = 5432

  db_subnet_group_name   = var.database_subnet_group_name
  vpc_security_group_ids = var.security_group_ids

  parameter_group_name    = var.parameter_group_name
  skip_final_snapshot    = var.skip_final_snapshot
  final_snapshot_identifier = "${var.name_prefix}-final-snapshot-${formatdate("YYYYMMDDhhmmss", timestamp())}"
  backup_retention_period = var.is_replica ? 0 : var.backup_retention_period
  backup_window          = var.backup_window
  maintenance_window     = var.maintenance_window

  replicate_source_db    = var.is_replica ? var.replicate_source_db : null
  publicly_accessible    = false
  multi_az               = false
  storage_encrypted      = var.storage_encrypted
  kms_key_id            = var.kms_key_arn

  monitoring_interval    = var.is_replica ? 0 : 60
  monitoring_role_arn    = var.is_replica ? null : aws_iam_role.rds_monitoring_role[0].arn

  tags = merge(
    var.tags,
    {
      Name = var.name_prefix
    }
  )

  lifecycle {
    ignore_changes = [
      replicate_source_db,
      password
    ]
  }
}

resource "aws_iam_role" "rds_monitoring_role" {
  count = var.is_replica ? 0 : 1

  name = "${var.name_prefix}-rds-monitoring-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "monitoring.rds.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "rds_monitoring_policy" {
  count = var.is_replica ? 0 : 1

  role       = aws_iam_role.rds_monitoring_role[0].name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonRDSEnhancedMonitoringRole"
}