package com.github.camellabs.component.tinkerforge.io;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeProducer;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;

import java.io.IOException;

public class IO4Producer extends TinkerforgeProducer<IO4Endpoint, BrickletIO4> {
    private final IO4Endpoint endpoint;

    public IO4Producer(final IO4Endpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletIO4.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletIO4 createBricklet(String uid, IPConnection connection) {
        return new BrickletIO4(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setConfiguration((short)(1 << endpoint.getIopin()), BrickletIO4.DIRECTION_OUT, false);
    }

    @Override
    public void process(Exchange exchange) throws TimeoutException, NotConnectedException, InvalidPayloadException {
        short iopin = exchange.getIn().getHeader("iopin", endpoint.getIopin(), Short.class);
        long duration = exchange.getIn().getHeader("duration", 0, Long.class);
        String message = exchange.getIn().getMandatoryBody(String.class);

        boolean value;
        switch (message) {
            case "false" :
            case "off" :
            case "0" :
            case "no" :
            case "low" :
                value = false;
                break;

            case "true" :
            case "on" :
            case "1" :
            case "yes" :
            case "high" :
                value = true;
                break;

            default:
                throw new IllegalArgumentException("Unrecognized message: "+message);
        }

        if (duration > 0) {
            log.debug("Setting pin "+iopin+" to value "+value+" for a duration of "+duration+" milliseconds. Message: "+message);
            bricklet.setMonoflop(iopin, iopin, duration);
        } else {
            log.debug("Setting pin "+iopin+" to value "+value+". Message: "+message);
            bricklet.setConfiguration(iopin, BrickletIO4.DIRECTION_OUT, value);
        }
    }
}