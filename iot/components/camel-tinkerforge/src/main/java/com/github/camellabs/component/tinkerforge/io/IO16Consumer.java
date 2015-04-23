package com.github.camellabs.component.tinkerforge.io;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;
import java.util.List;

public class IO16Consumer extends TinkerforgeConsumer<IO16Endpoint, BrickletIO16> implements BrickletIO16.InterruptListener {
    private final IO16Endpoint endpoint;

    public IO16Consumer(IO16Endpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletIO16.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletIO16 createBricklet(String uid, IPConnection connection) {
        return new BrickletIO16(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setPortConfiguration(endpoint.getIoport(), (short)255, BrickletIO16.DIRECTION_IN, true);
        bricklet.setPortInterrupt(endpoint.getIoport(), (short)255);
        bricklet.addInterruptListener(this);
        bricklet.setDebouncePeriod(endpoint.getDebounce());
    }

    @Override
    public void interrupt(char port, short interruptMask, short valueMask) {
        List<BinaryAnalyser.ResultSet> results = BinaryAnalyser.analyse(interruptMask, valueMask);
        for (BinaryAnalyser.ResultSet result : results) {
            Exchange exchange = null;
            try {
                exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, !result.value, bricklet.getIdentity());
                exchange.getIn().setHeader("com.tinkerforge.bricklet.io.pin", result.index-1); //correct 1-based
                exchange.getIn().setHeader("com.tinkerforge.bricklet.io.port", port);

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
}