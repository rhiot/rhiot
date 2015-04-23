package com.github.camellabs.component.tinkerforge.io;

import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriParam;

public class IO4Endpoint extends TinkerforgeEndpoint {
    @UriParam private String uid = "io4";
    @UriParam private String host = "localhost";
    @UriParam private int port = 4223;
    @UriParam private short iopin = 0;
    @UriParam private int debounce = 100;

    private IO4Producer producer;
    private IO4Consumer consumer;

    public IO4Endpoint(String uri, TinkerforgeComponent tinkerforgeComponent) {
		super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
        return producer != null ? producer : (producer = new IO4Producer(this));
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return consumer != null ? consumer : (consumer = new IO4Consumer(this, processor));
    }

    public short getIopin() {
        return iopin;
    }

    public void setIopin(short iopin) {
        this.iopin = iopin;
    }

    public int getDebounce() {
        return debounce;
    }

    public void setDebounce(int debounce) {
        this.debounce = debounce;
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

    @Override
	public boolean isSingleton() {
        return false;
    }
}