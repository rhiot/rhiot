/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.component.tinkerforge.dualrelay;

import java.io.IOException;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

import io.rhiot.component.tinkerforge.TinkerforgeComponent;
import io.rhiot.component.tinkerforge.TinkerforgeEndpoint;

@UriEndpoint(scheme = "tinkerforge", syntax = "tinkerforge:/dualrelay/<uid>", producerOnly=true, label = "iot", title = "Tinkerforge")
public class DualRelayEndpoint extends TinkerforgeEndpoint {
    @UriParam(name="relay", defaultValue="1")
    private short relay = 1;
    
    @UriParam(name="duration", defaultValue="0")
    private int duration = 0;

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
}
