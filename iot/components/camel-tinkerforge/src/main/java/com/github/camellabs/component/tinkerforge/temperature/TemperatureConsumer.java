package com.github.camellabs.component.tinkerforge.temperature;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.*;

import java.io.IOException;

public class TemperatureConsumer extends TinkerforgeConsumer<TemperatureEndpoint, BrickletTemperature> implements BrickletTemperature.TemperatureListener {
    private final TemperatureEndpoint endpoint;

    public TemperatureConsumer(TemperatureEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletTemperature.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletTemperature createBricklet(String uid, IPConnection connection) {
        return new BrickletTemperature(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setTemperatureCallbackPeriod(endpoint.getInterval());
        bricklet.addTemperatureListener(this);
    }

    @Override
    public void temperature(short temperature) {

        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, temperature, bricklet.getIdentity());
        } catch (TimeoutException | NotConnectedException e) {
            e.printStackTrace();
        }

        try {
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