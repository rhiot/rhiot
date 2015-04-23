package com.github.camellabs.component.tinkerforge;

import com.tinkerforge.*;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultConsumer;

import java.io.IOException;

public abstract class TinkerforgeConsumer<EndpointType extends TinkerforgeEndpoint, BrickletType extends Device> extends DefaultConsumer {
    protected IPConnection connection = new IPConnection();
    protected BrickletType bricklet;
    protected EndpointType endpoint;
    protected Processor processor;

    public TinkerforgeConsumer(final EndpointType endpoint, Processor processor, final int deviceIdentifier) throws IOException, AlreadyConnectedException {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.processor = processor;

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