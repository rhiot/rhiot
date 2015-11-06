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
package io.rhiot.cloudlets.device

import io.rhiot.mongodb.EmbeddedMongo
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.web.client.RestTemplate

import static com.github.camellabs.iot.cloudlet.device.client.LeshanClientTemplate.createGenericLeshanClientTemplate
import static com.google.common.truth.Truth.assertThat
import static io.rhiot.cloudlets.device.verticles.LeshanServerVerticle.UNKNOWN_DISCONNECTED
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setIntProperty
import static io.rhiot.utils.Uuids.uuid

class DeviceCloudletTest extends Assert {

    // Collaborators fixtures

    static def int restApiPort = findAvailableTcpPort()

    static def int lwm2mPort = findAvailableTcpPort()


    // Fixtures

    def apiBase = "http://localhost:${restApiPort}"

    def rest = new RestTemplate()

    def deviceId = uuid()

    // Collaborators setup

    @BeforeClass
    static void beforeClass() {
        new EmbeddedMongo().start()

        setIntProperty('api_rest_port', restApiPort)
        setIntProperty('disconnectionPeriod', 5000)
        setIntProperty('lwm2m_port', lwm2mPort)
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
        assertThat(response.devices.asType(List.class)).isEmpty()
    }

    @Test
    void shouldReturnVirtualClient() {
        // Given
        rest.delete("${apiBase}/device")

        // When
        rest.postForLocation("${apiBase}/device", new TestVirtualDevice(clientId: deviceId))
        def response = rest.getForObject(apiBase + '/device', Map.class)

        // Then
        assertThat(response.devices.asType(List.class)).hasSize(1)
    }

    @Test
    void shouldSendHeartbeatToVirtualDevice() {
        // Given
        rest.delete("${apiBase}/device")
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
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def device = rest.getForObject("${apiBase}/device/${deviceId}", Map.class)

        // Then
        assertThat(deviceId).isEqualTo(device['device']['endpoint'])
    }

    @Test
    void shouldListDeregisteredDevice() {
        // Given
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        rest.delete("${apiBase}/device/${deviceId}")

        // Then
        def client = rest.getForObject("${apiBase}/device/${deviceId}", Map.class)
        assertThat(client.client).isNull()
    }

    @Test
    void shouldListDisconnectedDevices() {
        // Given
        rest.delete("${apiBase}/device")
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        sleep(5000)
        def clients = rest.getForObject(new URI("http://localhost:${restApiPort}/device/disconnected"), Map.class)

        // Then
        assertEquals([deviceId], clients['disconnectedDevices'].asType(List.class))
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
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def manufacturer = rest.getForObject("${apiBase}/device/${deviceId}/manufacturer", Map.class)

        // Then
        assertThat(manufacturer.manufacturer).isEqualTo('Generic manufacturer')
    }

    @Test
    void shouldReadVirtualDeviceManufacturer() {
        // Given
        rest.postForLocation("${apiBase}/device", new TestVirtualDevice(clientId: deviceId))

        // When
        def manufacturer = rest.getForObject(new URI("http://localhost:${restApiPort}/device/${deviceId}/manufacturer"), Map.class)

        // Then
        assertThat(manufacturer.manufacturer).isEqualTo('Rhiot')
    }

    @Test
    void shouldReturnManufacturerFailureForNonExistingClient() {
        // Given
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def manufacturer = rest.getForObject(new URI("http://localhost:${restApiPort}/device/invalidEndpoint/manufacturer"), Map.class)

        // Then
        assertThat(manufacturer.failure).isNotNull()
    }

    @Test
    void shouldReturnUnknownManufacturer() {
        // Given
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect().disconnect()

        // When
        def manufacturer = rest.getForObject(new URI("http://localhost:${restApiPort}/device/${deviceId}/manufacturer"), Map.class)

        // Then
        assertThat(manufacturer.manufacturer).isEqualTo(UNKNOWN_DISCONNECTED)
    }

    @Test
    void shouldReadClientModel() {
        // Given
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def modelResponse = rest.getForObject("${apiBase}/device/${deviceId}/modelNumber", Map.class)

        // Then
        assertEquals('Generic model number', modelResponse.modelNumber)
    }

    @Test
    void shouldReadDeviceSerialNumber() {
        // Given
        createGenericLeshanClientTemplate(deviceId, lwm2mPort).connect()

        // When
        def serialNumberResponse = rest.getForObject("${apiBase}/device/${deviceId}/serialNumber", Map.class)

        // Then
        assertThat(serialNumberResponse.serialNumber).isEqualTo('Generic serial number')
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

    @Test
    void shouldReadVirtualDeviceDetails() {
        // Given
        rest.postForLocation("${apiBase}/device", new TestVirtualDevice(clientId: deviceId))

        // When
        def manufacturer = rest.getForObject(new URI("http://localhost:${restApiPort}/device/${deviceId}/details"), Map.class)

        // Then
        assertThat(manufacturer.deviceDetails.size()).isEqualTo(4)
    }

}

class TestVirtualDevice {

    String clientId

}