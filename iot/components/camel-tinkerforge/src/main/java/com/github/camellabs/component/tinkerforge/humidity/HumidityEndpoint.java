package com.github.camellabs.component.tinkerforge.humidity;

import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HumidityEndpoint extends TinkerforgeEndpoint {
    @UriParam private String uid = "h1";
    @UriParam private String host = "localhost";
    @UriParam private int port = 4223;
    @UriParam private int interval = 1000;

    private HumidityConsumer consumer;
    
    public HumidityEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) {
		super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
		throw new RuntimeCamelException("Cannot create a producer object since the brickletType 'humidity' cannot process information.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return consumer != null ? consumer : (consumer = new HumidityConsumer(this, processor));
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

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}