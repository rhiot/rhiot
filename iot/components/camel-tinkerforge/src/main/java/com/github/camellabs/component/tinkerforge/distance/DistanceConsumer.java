package com.github.camellabs.component.tinkerforge.distance;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import java.io.IOException;

public class DistanceConsumer extends TinkerforgeConsumer<DistanceEndpoint, BrickletDistanceIR> implements BrickletDistanceIR.DistanceListener {
	
    private final DistanceEndpoint endpoint;

    public DistanceConsumer(DistanceEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletDistanceIR.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletDistanceIR createBricklet(String uid, IPConnection connection) {
        return new BrickletDistanceIR(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setDistanceCallbackPeriod(endpoint.getInterval());
        bricklet.addDistanceListener(this);
    }

    @Override
    public void distance(int distance) {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, distance, bricklet.getIdentity());
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