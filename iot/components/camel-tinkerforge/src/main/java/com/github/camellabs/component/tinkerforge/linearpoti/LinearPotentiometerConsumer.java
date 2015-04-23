package com.github.camellabs.component.tinkerforge.linearpoti;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;

public class LinearPotentiometerConsumer extends TinkerforgeConsumer<LinearPotentiometerEndpoint, BrickletLinearPoti> implements BrickletLinearPoti.PositionListener {
    private final LinearPotentiometerEndpoint endpoint;

    public LinearPotentiometerConsumer(LinearPotentiometerEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletLinearPoti.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletLinearPoti createBricklet(String uid, IPConnection connection) {
        return new BrickletLinearPoti(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setPositionCallbackPeriod(endpoint.getInterval());
        bricklet.addPositionListener(this);
    }

    @Override
    public void position(int position) {
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