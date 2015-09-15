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
import org.eclipse.leshan.server.client.ClientRegistry
import org.eclipse.leshan.server.client.ClientRegistryListener
import org.eclipse.leshan.server.client.ClientUpdate

class CachingClientRegistry implements ClientRegistry {

    private final ClientRegistry clientRegistry

    private final CacheProvider cacheProvider

    CachingClientRegistry(ClientRegistry clientRegistry, CacheProvider cacheProvider) {
        this.clientRegistry = clientRegistry
        this.cacheProvider = cacheProvider
    }

    @Override
    Client get(String endpoint) {
        def client = cacheProvider.client(endpoint)
        if(client == null) {
            client = clientRegistry.get(endpoint);
            if(client == null) {
                return null
            }
            client = cacheProvider.updateClient(client)
        }
        client
    }

    @Override
    Collection<Client> allClients() {
        return clientRegistry.allClients()
    }

    @Override
    void addListener(ClientRegistryListener listener) {
        clientRegistry.addListener(listener)
    }

    @Override
    void removeListener(ClientRegistryListener listener) {
        clientRegistry.removeListener(listener)
    }

    @Override
    boolean registerClient(Client client) {
        cacheProvider.invalidate(client.endpoint)
        clientRegistry.registerClient(client)
    }

    @Override
    Client updateClient(ClientUpdate update) {
        def client = clientRegistry.updateClient(update)
        cacheProvider.invalidate(client.endpoint)
        client
    }

    @Override
    Client deregisterClient(String registrationId) {
        def client = clientRegistry.deregisterClient(registrationId)
        cacheProvider.invalidate(client.endpoint)
        client
    }

}
