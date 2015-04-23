package com.github.camellabs.component.tinkerforge;

import com.tinkerforge.*;
import org.apache.camel.impl.DefaultProducer;

import java.io.IOException;

public abstract class TinkerforgeProducer<EndpointType extends TinkerforgeEndpoint, BrickletType extends Device> extends DefaultProducer {
    protected IPConnection connection = new IPConnection();
    protected BrickletType bricklet;
    protected EndpointType endpoint;

    public TinkerforgeProducer(final EndpointType endpoint, final int deviceIdentifier) throws IOException, AlreadyConnectedException {
        super(endpoint);
        this.endpoint = endpoint;

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
}
