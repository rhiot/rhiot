package com.github.camellabs.component.tinkerforge.ledstrip;

import com.tinkerforge.BrickletLEDStrip;
import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.UriParam;

public class LedstripEndpoint extends TinkerforgeEndpoint {
    @UriParam protected String uid = "ls1";
    @UriParam protected String host = "localhost";
    @UriParam protected int port = 4223;
    @UriParam private int chipType = BrickletLEDStrip.CHIP_TYPE_WS2801;
    @UriParam private int frameDuration = 50;
    @UriParam private int amountOfLeds = 50;
    @UriParam private boolean throwExceptions = true;
    @UriParam private String rgbPattern = "rgb";

    private Producer producer;

    public LedstripEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) {
        super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
        return producer != null ? producer : (producer = new LedstripProducer(this));
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new RuntimeCamelException("Cannot create a consumer object since the brickletType 'ledstrip' has no input sensors.");
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getChipType() {
        return chipType;
    }

    public void setChipType(int chipType) {
        this.chipType = chipType;
    }

    public int getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(int frameDuration) {
        this.frameDuration = frameDuration;
    }

    public boolean getThrowExceptions() {
        return throwExceptions;
    }

    public void setThrowExceptions(boolean throwExceptions) {
        this.throwExceptions = throwExceptions;
    }

    public int getAmountOfLeds() {
        return amountOfLeds;
    }

    public void setAmountOfLeds(int amountOfLeds) {
        this.amountOfLeds = amountOfLeds;
    }

    public String getRgbPattern() {
        return rgbPattern;
    }

    public void setRgbPattern(String rgbPattern) {
        this.rgbPattern = rgbPattern;
    }

    @Override
	public boolean isSingleton() {
        return false;
    }
}