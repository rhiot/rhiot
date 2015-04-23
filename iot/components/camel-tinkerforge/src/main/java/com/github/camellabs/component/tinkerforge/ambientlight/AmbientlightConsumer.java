package com.github.camellabs.component.tinkerforge.ambientlight;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.io.IOException;

public class AmbientlightConsumer extends TinkerforgeConsumer<AmbientlightEndpoint, BrickletAmbientLight> implements BrickletAmbientLight.IlluminanceListener {
	
    private final AmbientlightEndpoint endpoint;

    public AmbientlightConsumer(AmbientlightEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletDistanceIR.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletAmbientLight createBricklet(String uid, IPConnection connection) {
        return new BrickletAmbientLight(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setIlluminanceCallbackPeriod(endpoint.getInterval());
        bricklet.addIlluminanceListener(this);
    }

    @Override
    public void illuminance(int illuminance) {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, illuminance, bricklet.getIdentity());
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