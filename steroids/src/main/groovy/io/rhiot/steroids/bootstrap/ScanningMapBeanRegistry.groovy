/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.steroids.bootstrap

import io.rhiot.steroids.Steroids

import static io.rhiot.utils.Uuids.uuid

class ScanningMapBeanRegistry extends MapBeanRegistry  {

    @Override
    def <T> Optional<T> bean(Class<T> type) {
        def cachedBean = super.bean(type)
        if(cachedBean.isPresent()) {
            return cachedBean
        }

        def scanResult = Steroids.bean(type)
        if(scanResult.isPresent()) {
            registry[type.simpleName + uuid()] = scanResult.get()
        }
        scanResult
    }

    @Override
    def <T> List<T> beans(Class<T> type) {
        def cachedBeans = super.beans(type)
        if(!cachedBeans.isEmpty()) {
            return cachedBeans
        }

        def scanResults = Steroids.beans(type)
        scanResults.forEach {
            registry[type.simpleName + uuid()] = it
        }
        scanResults
    }

}
