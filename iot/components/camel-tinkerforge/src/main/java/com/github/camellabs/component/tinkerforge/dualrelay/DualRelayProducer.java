package com.github.camellabs.component.tinkerforge.dualrelay;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeProducer;
import org.apache.camel.Exchange;

import java.io.IOException;

public class DualRelayProducer extends TinkerforgeProducer<DualRelayEndpoint, BrickletDualRelay> {

    public DualRelayProducer(DualRelayEndpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletDualRelay.DEVICE_IDENTIFIER);
    }

    @Override
    protected BrickletDualRelay createBricklet(String uid, IPConnection connection) {
        return new BrickletDualRelay(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
    }

    public void process(Exchange exchange) throws Exception {
        Short relay = exchange.getIn().getHeader("relay", endpoint.getRelay(), Short.class);
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
            bricklet.setSelectedState(relay, value);
            log.debug("Set relay "+relay+" to "+value);
        } else {
            bricklet.setMonoflop(relay, value, duration);
            log.debug("Set relay "+relay+" to "+value+" for duration "+duration+" ms.");
        }
    }
}
