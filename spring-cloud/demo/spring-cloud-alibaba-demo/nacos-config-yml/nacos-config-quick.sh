#!/bin/sh
echo "Nacos auto config started"

host=${1:-127.0.0.1:8848}
echo "host: $host"

groupId=${2:-xyz.ersut.demo.alibabacloud}
echo "groupId: $groupId"

applicationDevConfig=$(cat ./application-dev.yml)
curl -X POST "$host/nacos/v1/cs/configs" -d "type=yaml&dataId=application-dev.yml&group=${groupId}&content=${applicationDevConfig}"

alibabaGatewayConfig=$(cat ./alibaba-gateway.yml)
alibabaGatewayDevConfig=$(cat ./alibaba-gateway-dev.yml)
alibabaGatewayProdConfig=$(cat ./alibaba-gateway-prod.yml)
curl -X POST "$host/nacos/v1/cs/configs" -d "type=yaml&dataId=alibaba-gateway.yml&group=${groupId}&content=${alibabaGatewayConfig}"
curl -X POST "$host/nacos/v1/cs/configs" -d "type=yaml&dataId=alibaba-gateway-dev.yml&group=${groupId}&content=${alibabaGatewayDevConfig}"
curl -X POST "$host/nacos/v1/cs/configs" -d "type=yaml&dataId=alibaba-gateway-prod.yml&group=${groupId}&content=${alibabaGatewayProdConfig}"

sentinelApiConfig=$(cat ./sentinel/alibaba-gateway-sentinel-api.json)
sentinelFlowConfig=$(cat ./sentinel/alibaba-gateway-sentinel-flow.json)
curl -X POST "$host/nacos/v1/cs/configs" -d "type=json&dataId=alibaba-gateway-sentinel-api.json&group=${groupId}&content=${sentinelApiConfig}"
curl -X POST "$host/nacos/v1/cs/configs" -d "type=json&dataId=alibaba-gateway-sentinel-flow.json&group=${groupId}&content=${sentinelFlowConfig}"

sentinelServiceOrderFlowConfig=$(cat ./sentinel/service-order-flow.json)
curl -X POST "$host/nacos/v1/cs/configs" -d "type=json&dataId=service-order-flow.json&group=${groupId}&content=${sentinelServiceOrderFlowConfig}"
sentinelServiceOrderAuthorityConfig=$(cat ./sentinel/service-order-authority.json)
curl -X POST "$host/nacos/v1/cs/configs" -d "type=json&dataId=service-order-authority.json&group=${groupId}&content=${sentinelServiceOrderAuthorityConfig}"


echo "Nacos config pushed successfully finished"