package com.github.camellabs.component.tinkerforge;

import org.apache.camel.impl.DefaultEndpoint;

public abstract class TinkerforgeEndpoint extends DefaultEndpoint {

    public TinkerforgeEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) {
        super(uri, tinkerforgeComponent);
    }

    public abstract String getHost();
    public abstract int getPort();
    public abstract String getUid();
}
