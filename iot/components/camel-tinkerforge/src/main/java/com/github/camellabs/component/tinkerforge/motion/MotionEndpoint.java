package com.github.camellabs.component.tinkerforge.motion;

import com.github.camellabs.component.tinkerforge.TinkerforgeComponent;
import com.github.camellabs.component.tinkerforge.TinkerforgeEndpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.UriParam;

public class MotionEndpoint extends TinkerforgeEndpoint {
    @UriParam private String uid = "m1";
    @UriParam private String host = "localhost";
    @UriParam private int port = 4223;

    private MotionConsumer consumer;

    public MotionEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) {
		super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
		throw new RuntimeCamelException("Cannot create a producer object since the brickletType 'motion' cannot process information.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return consumer != null ? consumer : (consumer = new MotionConsumer(this, processor));
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