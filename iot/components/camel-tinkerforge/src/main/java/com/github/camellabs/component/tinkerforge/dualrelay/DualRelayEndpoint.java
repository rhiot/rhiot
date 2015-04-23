package com.github.camellabs.component.tinkerforge.dualrelay;

import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriParam;

import java.io.IOException;

public class DualRelayEndpoint extends TinkerforgeEndpoint {
    @UriParam private String uid = "dr1";
    @UriParam private String host = "localhost";
    @UriParam private int port = 4223;
    @UriParam private short relay = 1;
    @UriParam private int duration = 0;

	private DualRelayProducer producer;

    public DualRelayEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) throws IOException {
		super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
        return producer != null ? producer : (producer = new DualRelayProducer(this));
    }

    @Override
	public Consumer createConsumer(Processor processor) throws Exception {
		throw new Exception("Cannot create a consumer object since the brickletType 'dualrelay' cannot generate events.");
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

    public short getRelay() {
        return relay;
    }

    public void setRelay(Short relay) {
        this.relay = relay;
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
