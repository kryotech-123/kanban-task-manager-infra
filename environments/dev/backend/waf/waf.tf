variable "api_gateway_arn" {
  description = "arn of api gateway"
}
module "waf" {
  source = "../../../../modules/backend/waf"
  resource_arn = var.api_gateway_arn 
}