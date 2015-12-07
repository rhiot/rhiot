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
package io.rhiot.component.kura.gpio;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.eclipse.kura.gpio.GPIOService;
import org.eclipse.kura.gpio.KuraGPIODirection;
import org.eclipse.kura.gpio.KuraGPIOMode;
import org.eclipse.kura.gpio.KuraGPIOPin;
import org.eclipse.kura.gpio.KuraGPIOTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(scheme = "kura-gpio", syntax = "kura-gpio://gpioId", consumerClass = KuraGPIOConsumer.class, label = "platform,iot, kura", title = "Kura GPIO")
public class KuraGPIOEndpoint extends DefaultEndpoint {

    private static final transient Logger LOG = LoggerFactory.getLogger(KuraGPIOEndpoint.class);

    @UriPath(description = "gpioId")
    @Metadata(required = "true")
    private String gpioId;

    @UriParam(defaultValue = "false")
    private boolean state;

    @UriParam(defaultValue = "false")
    private boolean shutdownState;

    @UriParam(defaultValue = "0")
    private long delay;

    @UriParam(defaultValue = "50")
    private long duration = 50;

    @UriParam(defaultValue = "OUTPUT")
    private KuraGPIODirection direction = KuraGPIODirection.OUTPUT;

    @UriParam(defaultValue = "BOTH_EDGES")
    private KuraGPIOTrigger trigger = KuraGPIOTrigger.BOTH_EDGES;

    @UriParam(defaultValue = "OUTPUT_PUSH_PULL")
    private KuraGPIOMode mode = KuraGPIOMode.OUTPUT_PUSH_PULL;

    @UriParam(defaultValue = "TOGGLE")
    private KuraGPIOAction action = KuraGPIOAction.TOGGLE;

    private GPIOService service;

    public KuraGPIOEndpoint(String uri, KuraGPIOComponent kuraGPIOComponent, GPIOService service, String gpioId) {
        super(uri, kuraGPIOComponent);
        this.service = service;
        this.gpioId = gpioId;
    }

    private KuraGPIOPin retrieveGPIOPin() {
        try {
            return service.getPinByTerminal(Integer.parseInt(getGpioId()), direction, mode, trigger);
        } catch (NumberFormatException e) {
            return service.getPinByName(getGpioId(), direction, mode, trigger);
        }
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        KuraGPIOPin pin = retrieveGPIOPin();
        return new KuraGPIOConsumer(this, processor, pin);
    }

    @Override
    public Producer createProducer() throws Exception {
        KuraGPIOPin pin = retrieveGPIOPin();
        return new KuraGPIOProducer(this, pin);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getGpioId() {
        return gpioId;
    }

    public void setGpioId(String gpioId) {
        this.gpioId = gpioId;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public KuraGPIODirection getDirection() {
        return direction;
    }

    public void setDirection(KuraGPIODirection direction) {
        this.direction = direction;
    }

    public KuraGPIOTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(KuraGPIOTrigger trigger) {
        this.trigger = trigger;
    }

    public KuraGPIOMode getMode() {
        return mode;
    }

    public void setMode(KuraGPIOMode mode) {
        this.mode = mode;
    }

    public KuraGPIOAction getAction() {
        return action;
    }

    public void setAction(KuraGPIOAction action) {
        this.action = action;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isShutdownState() {
        return shutdownState;
    }

    public void setShutdownState(boolean shutdownState) {
        this.shutdownState = shutdownState;
    }

}
