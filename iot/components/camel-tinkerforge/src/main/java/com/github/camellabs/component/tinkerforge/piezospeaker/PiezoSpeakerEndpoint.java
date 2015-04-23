package com.github.camellabs.component.tinkerforge.piezospeaker;

import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PiezoSpeakerEndpoint extends TinkerforgeEndpoint {
    @UriParam private String uid = "ps1";
    @UriParam private String host = "localhost";
    @UriParam private int port = 4223;
    @UriParam private int frequency = 1000;
    @UriParam private int duration = 100;

	private PiezoSpeakerProducer producer;

    public PiezoSpeakerEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) throws IOException {
		super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
        return producer != null ? producer : (producer = new PiezoSpeakerProducer(this));
    }

    @Override
	public Consumer createConsumer(Processor processor) throws Exception {
		throw new Exception("Cannot create a consumer object since the brickletType 'piezospeaker' cannot generate events.");
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isSingleton() {
        return false;
    }
}
