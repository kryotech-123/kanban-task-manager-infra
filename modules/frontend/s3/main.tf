# This file contains the configuration for the S3 bucket used by the frontend application
# It sets up the S3 bucket with versioning, encryption, and a bucket policy to
# allow access from the CloudFront distribution's origin access identity

# Create an S3 bucket for the frontend application
resource "aws_s3_bucket" "frontend" {
  bucket        = var.bucket_name
  force_destroy = var.force_destroy

  tags = merge(
      var.tags,
      {
        Name = "${var.bucket_name}-frontend-bucket"
      }
    )
}


# Configure the S3 bucket to block public access
# This is important for security, ensuring that the bucket is not publicly accessible
resource "aws_s3_bucket_public_access_block" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}


# Enable versioning for the S3 bucket
# This allows for versioning of objects in the bucket, which is useful for data recovery and
# maintaining historical versions of files
resource "aws_s3_bucket_versioning" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  versioning_configuration {
    status = "Enabled"
  }
}

# Configure server-side encryption for the S3 bucket
# This ensures that all objects stored in the bucket are encrypted at rest
# Using AES256 encryption for server-side encryption
resource "aws_s3_bucket_server_side_encryption_configuration" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# Attach a bucket policy to the S3 bucket
# This policy allows the CloudFront origin access identity to access the S3 bucket
# It grants permission to get objects from the bucket, which is necessary for serving content via Cloud
resource "aws_s3_bucket_policy" "frontend" {
  bucket = aws_s3_bucket.frontend.id
  policy = data.aws_iam_policy_document.s3_policy.json
}

data "aws_iam_policy_document" "s3_policy" {
  statement {
    actions   = ["s3:GetObject"]
    resources = ["${aws_s3_bucket.frontend.arn}/*"]

    principals {
      type        = "AWS"
      identifiers = [var.cloudfront_origin_access_identity_iam_arn]
    }
  }
}