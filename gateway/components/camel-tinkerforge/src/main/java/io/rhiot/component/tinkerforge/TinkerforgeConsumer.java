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
package io.rhiot.component.tinkerforge;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public abstract class TinkerforgeConsumer<EndpointType extends TinkerforgeEndpoint, BrickletType extends Device> extends DefaultConsumer {
    protected IPConnection connection;
    protected BrickletType bricklet;
    protected EndpointType endpoint;
    protected Processor processor;
    private int deviceIdentifier;

    public TinkerforgeConsumer(final EndpointType endpoint, Processor processor, final int deviceIdentifier) throws IOException, AlreadyConnectedException {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.processor = processor;
        this.deviceIdentifier = deviceIdentifier;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        
        connection = new IPConnection();
        
        connection.addConnectedListener(new IPConnection.ConnectedListener() {
            @Override
            public void connected(short reason) {
                try {
                    connection.enumerate();
                } catch (NotConnectedException e) {
                    // Riiiiggght, are you kidding me?
                }
            }
        });
        connection.addDisconnectedListener(new IPConnection.DisconnectedListener() {
            @Override
            public void disconnected(short reason) {
                bricklet = null;
            }
        });
        connection.addEnumerateListener(new IPConnection.EnumerateListener() {
            @Override
            public void enumerate(String enumeratingUid, String connectedUid, char position, short[] hardwareVersion, short[] firmwareVersion, int enumeratingDeviceIdentifier, short enumerationType) {
                if (endpoint.getUid().equals(enumeratingUid)) {
                    if (enumerationType == IPConnection.ENUMERATION_TYPE_AVAILABLE ||
                        enumerationType == IPConnection.ENUMERATION_TYPE_CONNECTED)
                    {
                        if (enumeratingDeviceIdentifier == deviceIdentifier) {
                            try {
                                bricklet = createBricklet(enumeratingUid, connection);
                                configureBricklet();
                            } catch (TimeoutException | NotConnectedException e) {
                                log.error("Error setting bricklet configuration.", e);
                            }
                        } else {
                            throw new IllegalArgumentException("The bricklet with uid "+enumeratingUid+" is not the correct type of bricklet");
                        }
                    } else {
                        bricklet = null;
                    }
                }
            }
        });

        connection.connect(endpoint.getHost(),endpoint.getPort());
    }
    
    @Override
    protected void doStop() throws Exception {
        try {
            connection.disconnect();
        } catch (Exception e) {
            log.warn("Could not disconnect the connection properly", e);
        }
        super.doStop();
    }

    protected abstract BrickletType createBricklet(String uid, IPConnection connection);
    protected abstract void configureBricklet() throws TimeoutException, NotConnectedException;

    protected Exchange createOutOnlyExchangeWithBodyAndHeaders(org.apache.camel.Endpoint endpoint, Object messageBody, Device.Identity identity) {
        Exchange exchange = endpoint.createExchange(ExchangePattern.OutOnly);
        Message message = exchange.getIn();
        message.setHeader("com.tinkerforge.bricklet.uid", identity.uid);
        message.setHeader("com.tinkerforge.bricklet.connectedUid", identity.connectedUid);
        message.setHeader("com.tinkerforge.bricklet.deviceIdentifier", identity.deviceIdentifier);
        message.setHeader("com.tinkerforge.bricklet.position", identity.position);
        message.setBody(messageBody);
        return exchange;
    }
}