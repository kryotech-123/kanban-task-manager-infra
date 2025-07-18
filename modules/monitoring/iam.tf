# modules/monitoring/iam.tf
resource "aws_iam_role" "prod_log_forwarder" {
  name               = "${var.environment}-log-forwarder-role-prod"
  description        = "IAM role for CloudWatch Logs subscription forwarding"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action = "sts:AssumeRole",
      Effect = "Allow",
      Principal = {
        Service = "logs.amazonaws.com"
      }
    }]
  })
}



resource "aws_iam_role_policy_attachment" "prod_log_forwarder" {
  role       = aws_iam_role.prod_log_forwarder.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess" 
}
