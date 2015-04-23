package com.github.camellabs.component.tinkerforge.soundintensity;

import com.tinkerforge.*;
import com.github.camellabs.component.tinkerforge.TinkerforgeConsumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;

public class SoundIntensityConsumer extends TinkerforgeConsumer<SoundIntensityEndpoint, BrickletSoundIntensity> implements BrickletSoundIntensity.IntensityListener {
    private final SoundIntensityEndpoint endpoint;

    public SoundIntensityConsumer(SoundIntensityEndpoint endpoint, Processor processor) throws IOException, AlreadyConnectedException {
        super(endpoint, processor, BrickletSoundIntensity.DEVICE_IDENTIFIER);
        this.endpoint = endpoint;
    }

    @Override
    protected BrickletSoundIntensity createBricklet(String uid, IPConnection connection) {
        return new BrickletSoundIntensity(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setIntensityCallbackPeriod(endpoint.getInterval());
        bricklet.addIntensityListener(this);
    }

    @Override
    public void intensity(int intensity) {
        Exchange exchange = null;
        try {
            exchange = createOutOnlyExchangeWithBodyAndHeaders(endpoint, intensity, bricklet.getIdentity());
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