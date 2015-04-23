package com.github.camellabs.component.tinkerforge.rotarypoti;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;

public class RotaryPotentiometerConsumer extends TinkerforgeConsumer<RotaryPotentiometerEndpoint, BrickletRotaryPoti> implements BrickletRotaryPoti.PositionListener {
    private final RotaryPotentiometerEndpoint endpoint;

    public RotaryPotentiometerConsumer(RotaryPotentiometerEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletRotaryPoti.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletRotaryPoti createBricklet(String uid, IPConnection connection) {
        return new BrickletRotaryPoti(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setPositionCallbackPeriod(endpoint.getInterval());
        bricklet.addPositionListener(this);
    }

    @Override
    public void position(short position) {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, position, bricklet.getIdentity());
            getProcessor().process(exchange);
        } catch (Exception e) {
            getExceptionHandler().handleException("Error processing exchange", exchange, e);
        } finally {
            if (exchange != null && exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }
}