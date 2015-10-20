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
package io.rhiot.component.tinkerforge.io;

import io.rhiot.component.tinkerforge.TinkerforgeComponent;
import io.rhiot.component.tinkerforge.TinkerforgeEndpoint;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

@UriEndpoint(scheme = "tinkerforge", syntax = "tinkerforge:/io4/<uid>", consumerClass = IO4Consumer.class, label = "iot", title = "Tinkerforge")
public class IO4Endpoint extends TinkerforgeEndpoint {
    
    @UriParam(defaultValue="0")
    private short iopin = 0;
    
    @UriParam(defaultValue="100")
    private int debounce = 100;

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
}