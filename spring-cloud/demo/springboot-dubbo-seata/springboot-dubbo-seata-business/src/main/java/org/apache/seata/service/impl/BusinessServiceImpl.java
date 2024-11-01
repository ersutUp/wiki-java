/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seata.service.impl;

import org.apache.seata.core.context.RootContext;
import org.apache.seata.rm.datasource.exec.LockRetryController;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.seata.service.BusinessService;
import org.apache.seata.service.OrderService;
import org.apache.seata.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;

@Service
public class BusinessServiceImpl implements BusinessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessService.class);

    @DubboReference
    private StorageService storageService;
    @DubboReference
    private OrderService orderService;
    private final Random random = new Random();

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "spring-dubbo-tx")
    public void purchaseRollback(String userId, String commodityCode, int orderCount, int second) {
        LOGGER.info("purchase begin ... xid: " + RootContext.getXID());

        storageService.deduct(commodityCode, orderCount);
        // just test batch update
        //stockService.batchDeduct(commodityCode, orderCount);
        orderService.create(userId, commodityCode, orderCount);

        if (second > 0) {
            //这是为了卡主全局锁
            try {
                Thread.sleep(1000 * second);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException("random exception mock!");
    }

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "spring-dubbo-tx")
    public void purchaseCommit(String userId, String commodityCode, int orderCount) {
        LOGGER.info("purchase begin ... xid: " + RootContext.getXID());
        storageService.deduct(commodityCode, orderCount);
        // just test batch update
        //stockService.batchDeduct(commodityCode, orderCount);
        orderService.create(userId, commodityCode, orderCount);
    }

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "spring-dubbo-tx")
    public void commitSelectUpd(String userId, String commodityCode, int orderCount) {
        LOGGER.info("purchase begin ... xid: " + RootContext.getXID());
        storageService.deductSelectUpdate(commodityCode, orderCount);
        // just test batch update
        //stockService.batchDeduct(commodityCode, orderCount);
        orderService.create(userId, commodityCode, orderCount);
    }

    @Override
    @GlobalTransactional(timeoutMills = 300000, name = "spring-dubbo-tx")
    public void commitSelectUpdWaitGloblLock(String userId, String commodityCode, int orderCount) {
        LOGGER.info("purchase begin ... xid: " + RootContext.getXID());
        storageService.deductSelectUpdateWaitGloblLock(commodityCode, orderCount);
        // just test batch update
        //stockService.batchDeduct(commodityCode, orderCount);
        orderService.create(userId, commodityCode, orderCount);
    }
}