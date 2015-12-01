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
package io.rhiot.component.bluetooth;

import java.util.Set;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(scheme = "bluetooth", title = "Bluetooth", syntax = "bluetooth:label", consumerClass = BluetoothConsumer.class, label = "iot,messaging,bluetooth")
public class BluetoothEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(BluetoothEndpoint.class);

    @UriParam(defaultValue = "new BluecoveBluetoothDeviceProvider()")
    private BluetoothDevicesProvider bluetoothDevicesProvider;

    @UriParam(defaultValue = "false", description = "service discovery of bluetooth device")
    private boolean serviceDiscovery = false;

    public BluetoothEndpoint(String endpointUri, BluetoothComponent component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new BluetoothProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        BluetoothConsumer consumer = new BluetoothConsumer(this, processor);
        if (!getConsumerProperties().containsKey("delay")) {
            consumer.setDelay(5000);
        }
        configureConsumer(consumer);
        return consumer;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        getComponent().installDependencies();
        bluetoothDevicesProvider = resolveBluetoothDeviceProvider();
    }

    @Override
    public BluetoothComponent getComponent() {
        return (BluetoothComponent) super.getComponent();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Collaborators getters and setters

    public BluetoothDevicesProvider getBluetoothDevicesProvider() {
        return bluetoothDevicesProvider;
    }

    public void setBluetoothDevicesProvider(BluetoothDevicesProvider bluetoothDevicesProvider) {
        this.bluetoothDevicesProvider = bluetoothDevicesProvider;
    }

    public boolean isServiceDiscovery() {
        return serviceDiscovery;
    }

    public void setServiceDiscovery(boolean serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    // Helpers

    protected BluetoothDevicesProvider resolveBluetoothDeviceProvider() {
        if (bluetoothDevicesProvider == null) {
            Set<BluetoothDevicesProvider> providers = getCamelContext().getRegistry()
                    .findByType(BluetoothDevicesProvider.class);
            if (providers.size() == 1) {
                BluetoothDevicesProvider provider = providers.iterator().next();
                LOG.info("Found single instance of the BluetoothDevicesProvider in the registry. {} will be used.",
                        provider);
                return provider;
            } else if (getComponent().getBluetoothDevicesProvider() != null) {
                return getComponent().getBluetoothDevicesProvider();
            } else {
                return new BluecoveBluetoothDeviceProvider();
            }
        } else {
            return bluetoothDevicesProvider;
        }
    }
}
