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
package io.rhiot.cloudlets.device

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.web.client.RestTemplate

import static com.github.camellabs.iot.cloudlet.device.client.LeshanClientTemplate.createGenericLeshanClientTemplate
import static com.google.common.truth.Truth.assertThat
import static de.flapdoodle.embed.mongo.distribution.Version.V3_1_0
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Uuids.uuid
import static java.util.UUID.randomUUID

class DeviceCloudletTest extends Assert {

    static def int restApiPort = findAvailableTcpPort()

    static def int mongodbPort = findAvailableTcpPort()

    static def int lwm2mPort = findAvailableTcpPort()

    def apiBase = "http://localhost:${restApiPort}"

    def rest = new RestTemplate()

    def deviceId = uuid()

    // Collaborators setup

    @BeforeClass
    static void beforeClass() {
        System.setProperty('MONGODB_SERVICE_PORT', "${mongodbPort}")
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(V3_1_0)
                .net(new Net(mongodbPort, localhostIsIPv6()))
                .build();
        MongodStarter.getDefaultInstance().prepare(mongodConfig).start()

        System.setProperty('api_rest_port', "${restApiPort}")
        System.setProperty('disconnectionPeriod', "${5000}")
        System.setProperty('lwm2m_port', "${lwm2mPort}")

        new DeviceCloudlet().start().waitFor()
    }

    // Tests

    @Test
    void shouldReturnNoClients() {
        // Given
        rest.delete("${apiBase}/device")

        // When
        def response = rest.getForObject("${apiBase}/device", Map.class)

        // Then
        assertThat(response.devices.asType(List.class).size()).isEqualTo(0)
    }

    @Test
    void shouldReturnVirtualClient() {
        // Given
        rest.delete("${apiBase}/device")

        // When
        rest.postForLocation("${apiBase}/device", new TestVirtualDevice(clientId: uuid()))
        def response = rest.getForObject(apiBase + '/device', Map.class)

        // Then
        assertEquals(1, response['devices'].asType(List.class).size())
    }

    @Test
    void shouldSendHeartbeatToVirtualDevice() {
        // Given
        rest.delete("${apiBase}/device")
        def deviceId = uuid()
        rest.postForLocation("${apiBase}/device", new TestVirtualDevice(clientId: deviceId))
        sleep(5000)

        // When
        rest.getForEntity(apiBase + "/device/${deviceId}/heartbeat", Map.class)

        // Then
        def response = rest.getForObject(apiBase + '/device/disconnected', Map.class)
        assertEquals(0, response['disconnectedDevices'].asType(List.class).size())
    }

    @Test
    void shouldNotRegisterClientTwice() {
        // Given
        rest.delete("${apiBase}/device")
        def firstClient = 'foo'
        def secondClient = 'bar'

        // When
        createGenericLeshanClientTemplate(firstClient, lwm2mPort).connect()
        createGenericLeshanClientTemplate(firstClient, lwm2mPort).connect()
        createGenericLeshanClientTemplate(secondClient, lwm2mPort).connect()

        // Then
        def clients = rest.getForObject(new URI("http://localhost:${restApiPort}/device"), Map.class)
        assertEquals(2, clients['devices'].asType(List.class).size())
    }

    @Test
    void shouldListRegisteredDevices() {
        // Given
        def deviceId = uuid()
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def device = rest.getForObject("${apiBase}/device/${deviceId}", Map.class)

        // Then
        assertThat(deviceId).isEqualTo(device['client']['endpoint'])
    }

    @Test
    void shouldListDeregisteredDevice() {
        // Given
        def deviceId = uuid()
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        rest.delete("${apiBase}/device/${deviceId}")

        // Then
        def client = rest.getForObject("${apiBase}/device/${deviceId}", Map.class)
        assertThat(client.client).isNull()
    }

    @Test
    void shouldListDisconnectedClient() {
        // Given
        rest.delete("${apiBase}/device")
        def clientId = uuid()
        createGenericLeshanClientTemplate(clientId, lwm2mPort).connect()

        // When
        sleep(5000)
        def clients = rest.getForObject(new URI("http://localhost:${restApiPort}/device/disconnected"), Map.class)

        // Then
        assertEquals([clientId], clients['disconnectedDevices'].asType(List.class))
    }

    @Test
    void shouldNotListDisconnectedClient() {
        // Given
        rest.delete("${apiBase}/device")
        createGenericLeshanClientTemplate(uuid(), lwm2mPort).connect()

        // When
        def clients = rest.getForObject(new URI("http://localhost:${restApiPort}/device/disconnected"), Map.class)

        // Then
        assertEquals(0, clients['disconnectedDevices'].asType(List.class).size())
    }

    @Test
    void shouldReadClientManufacturer() {
        // Given
        def clientId = uuid()
        createGenericLeshanClientTemplate(clientId, lwm2mPort).connect()

        // When
        def manufacturer = rest.getForObject(new URI("http://localhost:${restApiPort}/device/${clientId}/manufacturer"), Map.class)

        // Then
        assertThat(manufacturer.manufacturer).isEqualTo('Generic manufacturer')
    }

    @Test
    void shouldReadVirtualDeviceManufacturer() {
        // Given
        def clientId = uuid()
        rest.postForLocation("${apiBase}/device", new TestVirtualDevice(clientId: clientId))

        // When
        def manufacturer = rest.getForObject(new URI("http://localhost:${restApiPort}/device/${clientId}/manufacturer"), Map.class)

        // Then
        assertThat(manufacturer.manufacturer).isEqualTo('Rhiot')
    }

    @Test
    void shouldReturnManufacturerFailureForNonExistingClient() {
        // Given
        createGenericLeshanClientTemplate(randomUUID().toString(), lwm2mPort).connect()

        // When
        def manufacturer = rest.getForObject(new URI("http://localhost:${restApiPort}/device/invalidEndpoint/manufacturer"), Map.class)

        // Then
        assertThat(manufacturer.failure).isNotNull()
    }

    @Test
    void shouldReadClientModel() {
        // Given
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def modelResponse = rest.getForObject("${apiBase}/device/${deviceId}/model", Map.class)

        // Then
        assertEquals('Generic model number', modelResponse.model)
    }

    @Test
    void shouldReadDeviceSerialNumber() {
        // Given
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def serialNumberResponse = rest.getForObject("${apiBase}/device/${deviceId}/serial", Map.class)

        // Then
        assertThat(serialNumberResponse.serial).isEqualTo('Generic serial number')
    }

    @Test
    void shouldReadDeviceFirmwareVersion() {
        // Given
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def firmwareResponse = rest.getForObject("${apiBase}/device/${deviceId}/firmwareVersion", Map.class)

        // Then
        assertThat(firmwareResponse.firmwareVersion).isEqualTo('1.0.0')
    }

}

class TestVirtualDevice {

    String clientId

}