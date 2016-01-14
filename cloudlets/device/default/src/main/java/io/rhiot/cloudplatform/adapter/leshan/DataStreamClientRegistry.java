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
package io.rhiot.cloudplatform.adapter.leshan;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.datastream.schema.device.Device;
import org.apache.camel.ProducerTemplate;
import org.eclipse.leshan.core.request.BindingMode;
import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistry;
import org.eclipse.leshan.server.client.ClientRegistryListener;
import org.eclipse.leshan.server.client.ClientUpdate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.rhiot.datastream.schema.device.DeviceConstants.*;
import static java.util.stream.Collectors.toList;

public class DataStreamClientRegistry implements ClientRegistry {

    private final List<ClientRegistryListener> listeners = new CopyOnWriteArrayList<>();

    private final PayloadEncoding payloadEncoding;

    private final ProducerTemplate producerTemplate;

    public DataStreamClientRegistry(PayloadEncoding payloadEncoding, ProducerTemplate producerTemplate) {
        this.payloadEncoding = payloadEncoding;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Client get(String endpoint) {
        byte[] response = producerTemplate.requestBody("amqp:" + getDevice(endpoint), null, byte[].class);
        Device device = (Device) payloadEncoding.decode(response);
        return new Client(null, device.getDeviceId(), null, 0, null);
    }

    @Override
    public Collection<Client> allClients() {
        byte[] response = producerTemplate.requestBody("amqp:" + listDevices(), null, byte[].class);
        List<Device> devices = (List<Device>) payloadEncoding.decode(response);
        return devices.stream().map(DataStreamClientRegistry::deviceToClient).collect(toList());
    }

    @Override
    public void addListener(ClientRegistryListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ClientRegistryListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean registerClient(Client client) {
        byte[] existingDeviceResponse = producerTemplate.requestBody("amqp:" + getDevice(client.getEndpoint()), null, byte[].class);
        Device existingDevice = (Device) payloadEncoding.decode(existingDeviceResponse);

        byte[] payload = payloadEncoding.encode(clientToDevice(client));
        byte[] response = producerTemplate.requestBody("amqp:" + registerDevice(), payload, byte[].class);
        try {
            payloadEncoding.decode(response);
            if(existingDevice != null) {
                Client existingClient = deviceToClient(existingDevice);
                listeners.stream().forEach(listener -> listener.unregistered(existingClient));
            }
            listeners.stream().forEach(listener -> listener.registered(client));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Client updateClient(ClientUpdate update) {
        byte[] existingDeviceResponse = producerTemplate.requestBody("amqp:" + getDeviceByRegistrationId(update.getRegistrationId()), null, byte[].class);
        Map<String, Object> decodedDevicePayload = (Map<String, Object>) payloadEncoding.decode(existingDeviceResponse);
        Device existingDevice = new ObjectMapper().convertValue(decodedDevicePayload, Device.class);
        if(existingDevice == null) {
            return null;
        }

        Client client = update.updateClient(deviceToClient(existingDevice));
        byte[] encodedDevice = payloadEncoding.encode(clientToDevice(client));
        byte[] response = producerTemplate.requestBody("amqp:" + updateDevice(), encodedDevice, byte[].class);
        payloadEncoding.decode(response);

        // notify listener
        for (ClientRegistryListener l : listeners) {
            l.updated(client);
        }
        return client;
    }

    @Override
    public Client deregisterClient(String registrationId) {
        byte[] response = producerTemplate.requestBody("amqp:device.deregister." + registrationId, null, byte[].class);
        Device device = (Device) payloadEncoding.decode(response);
        Client client = new Client(null, device.getDeviceId(), null, 0, null);
        listeners.stream().forEach(listener -> listener.unregistered(client));
        return client;
    }

    // Helpers

    private static Client deviceToClient(Device device) {
        return new Client(
                device.getRegistrationId(), device.getDeviceId(),
                device.getAddress(), device.getPort(), device.getLwM2mVersion(), device.getLifeTimeInSec(), device.getSmsNumber(), BindingMode.valueOf(device.getBindingMode().name()), null, device.getRegistrationEndpointAddress(),
                device.getRegistrationDate(), device.getLastUpdate()
        );
    }

    private static Device clientToDevice(Client client) {
        return new Device(
                client.getEndpoint(), client.getRegistrationId(),
                client.getRegistrationDate(), client.getLastUpdate(),
                client.getAddress(), client.getPort(), client.getRegistrationEndpointAddress(),
                client.getLifeTimeInSec(), client.getSmsNumber(), client.getLwM2mVersion(), Device.BindingMode.valueOf(client.getBindingMode().name())
        );
    }

}
