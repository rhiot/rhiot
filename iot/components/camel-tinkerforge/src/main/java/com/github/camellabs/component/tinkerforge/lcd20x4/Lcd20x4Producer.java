package com.github.camellabs.component.tinkerforge.lcd20x4;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeProducer;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Lcd20x4Producer extends TinkerforgeProducer<Lcd20x4Endpoint, BrickletLCD20x4> {
    private Lcd20x4Endpoint endpoint;
    
    public Lcd20x4Producer(Lcd20x4Endpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletLCD20x4.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletLCD20x4 createBricklet(String uid, IPConnection connection) {
        return new BrickletLCD20x4(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.clearDisplay();
        bricklet.setConfig(endpoint.getCursor(), endpoint.getBlink());
        bricklet.backlightOn();
    }

    public void process(Exchange exchange) throws InvalidPayloadException, TimeoutException, NotConnectedException {
        short line = exchange.getIn().getHeader("line", endpoint.getLine(), Short.class);
        short position = exchange.getIn().getHeader("position", endpoint.getPosition(), Short.class);

        bricklet.writeLine(line, position, exchange.getIn().getMandatoryBody(String.class));
    	log.debug("Wrote text '"+exchange.getIn().getBody(String.class)+ "' to line "+line+" on position "+position);
    }
}