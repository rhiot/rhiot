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
package com.github.camellabs.iot.cloudlet.device.leshan

import org.eclipse.leshan.server.client.Client
import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager

class InfinispanCacheProvider implements CacheProvider {

    private final EmbeddedCacheManager cacheManager

    InfinispanCacheProvider(EmbeddedCacheManager cacheManager) {
        this.cacheManager = cacheManager
        eagerlyInitializeCache()
    }

    @Override
    Client client(String endpoint) {
        cache().get(endpoint)
    }

    @Override
    Client updateClient(Client client) {
        cache().put(client.endpoint, client)
        return client
    }

    @Override
    void invalidate(String endpoint) {
        cache().remove(endpoint)
    }

    private Cache<String, Client> cache() {
        cacheManager.getCache('clients')
    }

    private def eagerlyInitializeCache() {
        cache()
    }

}
