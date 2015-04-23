package com.github.camellabs.component.tinkerforge.io;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeProducer;
import org.apache.camel.Exchange;

import java.io.IOException;

public class IO16Producer extends TinkerforgeProducer<IO16Endpoint, BrickletIO16> {
    private final IO16Endpoint endpoint;

    public IO16Producer(final IO16Endpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletIO16.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletIO16 createBricklet(String uid, IPConnection connection) {
        return new BrickletIO16(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setPortConfiguration(endpoint.getIoport(), (short)255, BrickletIO16.DIRECTION_OUT, false);
    }

    @Override
    public void process(Exchange exchange) throws TimeoutException, NotConnectedException {
        char port = exchange.getIn().getHeader("port", endpoint.getIoport(), Character.class);
        long duration = exchange.getIn().getHeader("duration", 0, Long.class);
        String message = exchange.getIn().getBody() != null ? exchange.getIn().getBody(String.class) : "null";

        short iopin;
        if (exchange.getIn().getHeader("iopin") == null) {
            iopin = 255;
        } else {
            iopin = (short)(1 << exchange.getIn().getHeader("iopin",Integer.class));
        }

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
            log.debug("Setting pins in pattern "+iopin+" of port "+port+" to value "+value+" for a duration of "+duration+" milliseconds. Message: "+message);
            bricklet.setPortMonoflop(port, iopin, iopin, duration);
        } else {
            log.debug("Setting pins in pattern "+iopin+" of port "+port+" to value "+value+". Message: "+message);
            bricklet.setPortConfiguration(port, iopin, BrickletIO16.DIRECTION_OUT, value);
        }
    }
}