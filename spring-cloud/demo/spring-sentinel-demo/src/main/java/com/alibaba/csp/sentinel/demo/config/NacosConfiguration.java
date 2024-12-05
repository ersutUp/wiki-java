/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.demo.config;

import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Eric Zhao
 */
@Configuration
@EnableConfigurationProperties
public class NacosConfiguration {

    @Bean
    public ReadableDataSource sentinelFlowRuleNacosConf(SentinelNacosProperties sentinelNacosProperties) {
        // remoteAddress 代表 Nacos 服务端的地址
        // groupId 和 dataId 对应 Nacos 中相应配置
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(
                sentinelNacosProperties.getRemoteAddress(),
                sentinelNacosProperties.getGroupId(),
                sentinelNacosProperties.getFlowDataId(),
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {})
        );

        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        return flowRuleDataSource;

    }
    @Bean
    public ReadableDataSource SentinelDegradeRuleNacosConf(SentinelNacosProperties sentinelNacosProperties) {
        // remoteAddress 代表 Nacos 服务端的地址
        // groupId 和 dataId 对应 Nacos 中相应配置
        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new NacosDataSource<>(
                sentinelNacosProperties.getRemoteAddress(),
                sentinelNacosProperties.getGroupId(),
                sentinelNacosProperties.getDegradeDataId(),
                source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {})
        );

        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());

        return degradeRuleDataSource;

    }


}
