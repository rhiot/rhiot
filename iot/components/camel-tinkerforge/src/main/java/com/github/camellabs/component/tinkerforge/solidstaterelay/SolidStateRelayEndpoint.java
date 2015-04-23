package com.github.camellabs.component.tinkerforge.solidstaterelay;

import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriParam;

import java.io.IOException;

public class SolidStateRelayEndpoint extends TinkerforgeEndpoint {
    @UriParam private String uid = "Ssr1";
    @UriParam private String host = "localhost";
    @UriParam private int port = 4223;
    @UriParam private int duration = 0;

	private SolidStateRelayProducer producer;

    public SolidStateRelayEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) throws IOException {
		super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
        return producer != null ? producer : (producer = new SolidStateRelayProducer(this));
    }

    @Override
	public Consumer createConsumer(Processor processor) throws Exception {
		throw new Exception("Cannot create a consumer object since the brickletType 'solidstaterelay' cannot generate events.");
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

    public boolean isSingleton() {
        return false;
    }
}
