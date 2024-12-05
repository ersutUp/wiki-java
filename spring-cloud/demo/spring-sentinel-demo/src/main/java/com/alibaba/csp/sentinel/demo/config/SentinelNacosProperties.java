package com.alibaba.csp.sentinel.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "sentinel.nacos")
@Component
public class SentinelNacosProperties{

    public String remoteAddress;

    public String groupId;

    public String flowDataId;

    public String degradeDataId;

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFlowDataId() {
        return flowDataId;
    }

    public void setFlowDataId(String flowDataId) {
        this.flowDataId = flowDataId;
    }

    public String getDegradeDataId() {
        return degradeDataId;
    }

    public void setDegradeDataId(String degradeDataId) {
        this.degradeDataId = degradeDataId;
    }
}
