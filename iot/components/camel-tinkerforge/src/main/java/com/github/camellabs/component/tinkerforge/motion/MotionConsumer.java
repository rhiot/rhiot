package com.github.camellabs.component.tinkerforge.motion;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;

public class MotionConsumer extends TinkerforgeConsumer<MotionEndpoint, BrickletMotionDetector> implements BrickletMotionDetector.MotionDetectedListener, BrickletMotionDetector.DetectionCycleEndedListener {
	
    private final MotionEndpoint endpoint;

    public MotionConsumer(MotionEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletMotionDetector.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletMotionDetector createBricklet(String uid, IPConnection connection) {
        return new BrickletMotionDetector(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.addMotionDetectedListener(this);
        bricklet.addDetectionCycleEndedListener(this);
    }

    @Override
    public void motionDetected() {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, "motion detected" , bricklet.getIdentity());
            getProcessor().process(exchange);
        } catch (Exception e) {
            getExceptionHandler().handleException("Error processing exchange", exchange, e);
        } finally {
            if (exchange != null && exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
    }

    @Override
    public void detectionCycleEnded() {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, "motion detection cycle ended" , bricklet.getIdentity());
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