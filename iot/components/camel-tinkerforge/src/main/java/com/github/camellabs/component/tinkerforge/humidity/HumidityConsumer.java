package com.github.camellabs.component.tinkerforge.humidity;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HumidityConsumer extends TinkerforgeConsumer<HumidityEndpoint, BrickletHumidity> implements BrickletHumidity.HumidityListener {
    private final HumidityEndpoint endpoint;

    public HumidityConsumer(HumidityEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletHumidity.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletHumidity createBricklet(String uid, IPConnection connection) {
        return new BrickletHumidity(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setHumidityCallbackPeriod(endpoint.getInterval());
        bricklet.addHumidityListener(this);
    }

    @Override
    public void humidity(int humidity) {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, humidity, bricklet.getIdentity());
        } catch (TimeoutException | NotConnectedException e) {
            e.printStackTrace();
        }

        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            getExceptionHandler().handleException("Error processing exchange", exchange, e);
        } finally {
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }
}