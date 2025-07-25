# **Kanban Task Manager - Frontend Infrastructure Documentation**  
**Terraform Deployment Guide (AWS)**  

---

## **📁 Infrastructure Overview**  
The frontend infrastructure consists of:  
- **AWS WAF** (Web Application Firewall) for security  
- **S3 Bucket** for hosting static Angular files  
- **CloudFront** CDN for global distribution  
- **Random ID Generator** for unique resource naming  

### **📂 Directory Structure**  
```
modules/frontend/  
├── cloudfront/          # CloudFront CDN configuration  
│   ├── main.tf          # CloudFront distribution setup  
│   ├── outputs.tf       # Outputs (ARN, domain name, etc.)  
│   └── variables.tf     # Input variables (ACM cert, aliases, etc.)  
├── random_id/           # Random suffix generator  
│   └── main.tf          # Creates a unique ID for S3 bucket names  
├── s3/                  # S3 bucket for frontend assets  
│   ├── main.tf          # Bucket config, policies, encryption  
│   ├── outputs.tf       # Bucket ARN, domain name  
│   └── variables.tf     # Input variables (bucket name, OAI ARN)  
└── waf/                 # Web Application Firewall  
    ├── main.tf          # WAF rules (rate limiting, SQLi, etc.)  
    ├── outputs.tf       # WAF ARN for CloudFront  
    └── variables.tf     # Input variables (app name, tags)  
```

---

## **⚙️ Core Components**  

### **1. Terraform Backend (State Management)**  
- **S3 Bucket:** `kanban-task-manager-terraform-state-files` (stores Terraform state)  
- **DynamoDB Table:** `terraform-lock-table-dev` (prevents concurrent state modifications)  
- **Region:** `eu-west-1`  

### **2. AWS Provider**  
- Configured to deploy resources in `eu-west-1`.  

### **3. Modules**  

#### **🛡️ WAF (Web Application Firewall)**  
- **Protections Enabled:**  
  - **AWS Managed Rules** (Common attacks, SQLi, bad inputs)  
  - **Rate Limiting** (1000 requests/IP)  
- **Output:** `web_acl_arn` → Attached to CloudFront.  

#### **📦 S3 Bucket**  
- **Bucket Name:** `${var.bucket_name}-{random_id}` (e.g., `kanban-frontend-dev-a1b2c3`)  
- **Security:**  
  - **Block all public access**  
  - **Encrypted (AES-256)**  
  - **Access restricted to CloudFront (OAI)**  

#### **🌍 CloudFront CDN**  
- **Origin:** S3 bucket (private, accessed via OAI)  
- **Security:**  
  - **WAF integration** (`web_acl_arn` from WAF module)  
  - **HTTPS enforced (TLS 1.2+)**  
- **Error Handling:**  
  - 403/404 errors redirect to `index.html` (SPA support)  

#### **🎲 Random ID Generator**  
- Generates a unique suffix (`hex`) for S3 bucket names to avoid collisions.  

---

## **🚀 Deployment Steps**  

### **1. Initialize Terraform**  
```bash
cd environments/dev/
terraform init -backend-config=backend.tfvars
```

### **2. Apply Configuration**  
```bash
terraform apply -var-file=terraform.tfvars
```

### **3. Verify Deployment**  
- **S3 Bucket:** Check in AWS Console → `kanban-frontend-dev-{random_id}`  
- **CloudFront:** Verify distribution status is "Deployed"  
- **WAF:** Check rules in AWS WAF Console  

---

## **🔒 Security & Best Practices**  
✅ **WAF Protection** – Blocks SQLi, XSS, excessive requests.  
✅ **Private S3 Bucket** – Only accessible via CloudFront.  
✅ **Encryption** – S3 (AES-256), CloudFront (TLS 1.2+).  
✅ **State Locking** – DynamoDB prevents concurrent modifications.  

---

## **📝 Next Steps**  
1. **Backend Setup** (ECS + API Gateway)  
2. **CI/CD Pipeline** (Automate frontend deployments)  
3. **Monitoring** (CloudWatch alarms for WAF/CloudFront)  

---
 

