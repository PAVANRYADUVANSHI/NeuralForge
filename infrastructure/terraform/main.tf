terraform {
  required_version = ">= 1.6"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

variable "aws_region" {
  default = "us-east-1"
}

variable "cluster_name" {
  default = "neuralforge-cluster"
}

# VPC
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "neuralforge-vpc"
  }
}

# Subnets
resource "aws_subnet" "public_1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "${var.aws_region}a"
  map_public_ip_on_launch = true

  tags = {
    Name = "neuralforge-public-1"
  }
}

resource "aws_subnet" "public_2" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "${var.aws_region}b"
  map_public_ip_on_launch = true

  tags = {
    Name = "neuralforge-public-2"
  }
}

# EKS Cluster
resource "aws_eks_cluster" "main" {
  name     = var.cluster_name
  role_arn = aws_iam_role.eks_cluster.arn

  vpc_config {
    subnet_ids = [aws_subnet.public_1.id, aws_subnet.public_2.id]
  }

  depends_on = [aws_iam_role_policy_attachment.eks_cluster_policy]
}

resource "aws_iam_role" "eks_cluster" {
  name = "neuralforge-eks-cluster-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "eks.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "eks_cluster_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eks_cluster.name
}

# RDS PostgreSQL
resource "aws_db_instance" "postgres" {
  identifier           = "neuralforge-db"
  engine               = "postgres"
  engine_version       = "16.1"
  instance_class       = "db.t3.medium"
  allocated_storage    = 100
  storage_type         = "gp3"
  db_name              = "neuralforge"
  username             = "neuralforge_admin"
  password             = var.db_password
  skip_final_snapshot  = true
  publicly_accessible  = false

  tags = {
    Name = "neuralforge-postgres"
  }
}

variable "db_password" {
  type      = string
  sensitive = true
}

# ElastiCache Redis
resource "aws_elasticache_cluster" "redis" {
  cluster_id           = "neuralforge-redis"
  engine               = "redis"
  node_type            = "cache.t3.medium"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis7"
  engine_version       = "7.0"
  port                 = 6379
}

# S3 Bucket
resource "aws_s3_bucket" "artifacts" {
  bucket = "neuralforge-artifacts-${data.aws_caller_identity.current.account_id}"

  tags = {
    Name = "neuralforge-artifacts"
  }
}

data "aws_caller_identity" "current" {}

output "eks_cluster_endpoint" {
  value = aws_eks_cluster.main.endpoint
}

output "rds_endpoint" {
  value = aws_db_instance.postgres.endpoint
}

output "redis_endpoint" {
  value = aws_elasticache_cluster.redis.cache_nodes[0].address
}
