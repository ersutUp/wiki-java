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
package org.apache.seata.service;

public interface StorageService {

    /**
     * 扣减库存
     *
     * @param commodityCode 商品编号
     * @param count         扣减数量
     */
    void deduct(String commodityCode, int count);

    /**
     * 扣减库存
     *
     * @param commodityCode 商品编号
     * @param count         扣减数量
     */
    void deductSelectUpdate(String commodityCode, int count);


    void deductSelectUpdateWaitGloblLock(String commodityCode, int count);

        /**
         * 批量扣减
         *
         * @param commodityCode
         * @param count
         */
    void batchDeduct(String commodityCode, int count);
}