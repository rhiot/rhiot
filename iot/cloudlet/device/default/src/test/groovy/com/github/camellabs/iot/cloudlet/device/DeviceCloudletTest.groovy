/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.cloudlet.device

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.util.SocketUtils
import org.springframework.web.client.RestTemplate

import static com.github.camellabs.iot.cloudlet.device.DeviceCloudlet.jackson
import static com.github.camellabs.iot.cloudlet.device.client.DefaultLeshanClient.createLeshanCloudClient
import static de.flapdoodle.embed.mongo.distribution.Version.V3_1_0
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6
import static java.util.UUID.randomUUID
import static org.springframework.util.SocketUtils.findAvailableTcpPort

class DeviceCloudletTest extends Assert {

    static def int restApiPort = findAvailableTcpPort()

    @BeforeClass
    static void beforeClass() {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(V3_1_0)
                .net(new Net(27017, localhostIsIPv6()))
                .build();
        MongodStarter.getDefaultInstance().prepare(mongodConfig).start()

        System.setProperty('camellabs_iot_cloudlet_device_api_rest_port', "${restApiPort}")
        System.setProperty('camellabs_iot_cloudlet_device_disconnectionPeriod', "${5000}")

        new DeviceCloudlet().start()
    }

    @Test
    void shouldReturnNoClients() {
        new RestTemplate().delete(new URI("http://localhost:${restApiPort}/client"))
        def response = new RestTemplate().getForObject(new URI("http://localhost:${restApiPort}/client"), Map.class)
        assertEquals(0, response['clients'].asType(List.class).size())
    }

    @Test
    void shouldNotRegisterClientTwice() {
        // Given
        new RestTemplate().delete(new URI("http://localhost:${restApiPort}/client"))
        def firstClient = 'foo'
        def secondClient = 'bar'

        // When
        createLeshanCloudClient(firstClient).connect()
        createLeshanCloudClient(firstClient).connect()
        createLeshanCloudClient(secondClient).connect()

        // Then
        def clients = new RestTemplate().getForObject(new URI("http://localhost:${restApiPort}/client"), Map.class)
        assertEquals(2, clients['clients'].asType(List.class).size())
    }

    @Test
    void shouldListRegisteredClient() {
        // Given
        def clientId = randomUUID().toString()
        createLeshanCloudClient(clientId).connect()

        // When
        def client = new RestTemplate().getForObject(new URI("http://localhost:${restApiPort}/client/${clientId}"), Map.class)

        // Then
        assertEquals(clientId, client['client']['endpoint'])
    }

    @Test
    void shouldListDisconnectedClient() {
        // Given
        new RestTemplate().delete(new URI("http://localhost:${restApiPort}/client"))
        def clientId = randomUUID().toString()
        createLeshanCloudClient(clientId).connect()

        // When
        sleep(5000)
        def clients = new RestTemplate().getForObject(new URI("http://localhost:${restApiPort}/clients/disconnected"), Map.class)

        // Then
        assertEquals([clientId], clients['disconnectedClients'].asType(List.class))
    }

    @Test
    void shouldNotListDisconnectedClient() {
        // Given
        new RestTemplate().delete(new URI("http://localhost:${restApiPort}/client"))
        def clientId = randomUUID().toString()
        createLeshanCloudClient(clientId).connect()

        // When
        def clients = new RestTemplate().getForObject(new URI("http://localhost:${restApiPort}/clients/disconnected"), Map.class)

        // Then
        assertEquals(0, clients['disconnectedClients'].asType(List.class).size())
    }

    @Test
    void shouldReadClientManufacturer() {
        // Given
        def clientId = randomUUID().toString()
        createLeshanCloudClient(clientId).connect()

        // When
        def manufacturer = new RestTemplate().getForObject(new URI("http://localhost:${restApiPort}/client/${clientId}/manufacturer"), Map.class)

        // Then
        assertEquals('Generic manufacturer', manufacturer['manufacturer'])
    }

    @Test
    void shouldReturnManufacturerFailureForNonExistingClient() {
        // Given
        createLeshanCloudClient(randomUUID().toString()).connect()

        // When
        def manufacturer = new RestTemplate().getForObject(new URI("http://localhost:${restApiPort}/client/invalidEndpoint/manufacturer"), Map.class)

        // Then
        assertNotNull('Generic manufacturer', manufacturer['failure'])
    }

}
