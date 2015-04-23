package com.github.camellabs.component.tinkerforge.piezospeaker;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeProducer;
import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;

import java.io.IOException;

public class PiezoSpeakerProducer extends TinkerforgeProducer<PiezoSpeakerEndpoint, BrickletPiezoSpeaker> {

    public PiezoSpeakerProducer(PiezoSpeakerEndpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletPiezoSpeaker.DEVICE_IDENTIFIER);
    }

    @Override
    protected BrickletPiezoSpeaker createBricklet(String uid, IPConnection connection) {
        return new BrickletPiezoSpeaker(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
    }

    public void process(Exchange exchange) throws Exception {
        Integer frequency = exchange.getIn().getHeader("frequency", endpoint.getFrequency(), Integer.class);
        Integer duration = exchange.getIn().getHeader("duration", endpoint.getDuration(), Integer.class);

        bricklet.beep(duration, frequency);
    	log.debug("Started beep with duration "+duration+"ms and frequency "+frequency+"hz");
    }
}
