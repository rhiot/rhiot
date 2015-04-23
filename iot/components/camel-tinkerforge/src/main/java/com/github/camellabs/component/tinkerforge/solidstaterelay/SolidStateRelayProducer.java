package com.github.camellabs.component.tinkerforge.solidstaterelay;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeProducer;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;

import java.io.IOException;

public class SolidStateRelayProducer extends TinkerforgeProducer<SolidStateRelayEndpoint, BrickletSolidStateRelay> {

    public SolidStateRelayProducer(SolidStateRelayEndpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletSolidStateRelay.DEVICE_IDENTIFIER);
    }

    @Override
    protected BrickletSolidStateRelay createBricklet(String uid, IPConnection connection) {
        return new BrickletSolidStateRelay(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
    }

    public void process(Exchange exchange) throws InvalidPayloadException, TimeoutException, NotConnectedException {
        Integer duration = exchange.getIn().getHeader("duration", endpoint.getDuration(), Integer.class);
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

        if (duration == 0) {
            bricklet.setState(value);
            log.debug("Set relay to "+value);
        } else {
            bricklet.setMonoflop(value, duration);
            log.debug("Set relay to "+value+" for duration "+duration+" ms.");
        }
    }
}
