package com.github.camellabs.component.tinkerforge.rotarypoti;

import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriParam;

public class RotaryPotentiometerEndpoint extends TinkerforgeEndpoint {
    @UriParam private String uid = "rp1";
    @UriParam private String host = "localhost";
    @UriParam private int port = 4223;
    @UriParam private int interval = 1000;

    private RotaryPotentiometerConsumer consumer;

    public RotaryPotentiometerEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) {
		super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
		throw new Exception("Cannot create a producer object since the brickletType 'rotarypoti' cannot process information.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return consumer != null ? consumer : (consumer = new RotaryPotentiometerConsumer(this, processor));
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

	public boolean isSingleton() {
        return false;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}