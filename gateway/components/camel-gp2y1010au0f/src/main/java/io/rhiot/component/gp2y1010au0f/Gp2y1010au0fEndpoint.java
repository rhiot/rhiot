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
package io.rhiot.component.gp2y1010au0f;

import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiChannel;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;

@UriEndpoint(scheme = "gp2y1010au0f", title = "Gp2y1010au0f", syntax = "gp2y1010au0f:name", consumerClass = Gp2y1010au0fConsumer.class, label = "iot,messaging,gp2y1010au0f")
public class Gp2y1010au0fEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(Gp2y1010au0fEndpoint.class);

    @UriParam(defaultValue = "0", description = "spi channelId")
    private int channelId = 0;

    @UriPath(defaultValue = "GPIO 1", description = "gp2y1010au0f configuration pin")
    private String iled = "1";

    @UriPath(defaultValue = "0", description = "digital output of gp2y1010au0f readings")
    private int analogAddress = 0;

    @UriPath(defaultValue = "0", description = "how long sampling should take")
    private int samplingDelay = 0;

    private Class gpioClass = RaspiPin.class;

    private GpioPinDigitalOutput iledPin = null;
    private MCP3008GpioProvider mcp3008GpioProvider = null;
    private Pin analogPin;

    public Gp2y1010au0fEndpoint(String endpointUri, Gp2y1010au0fComponent component) {
        super(endpointUri, component);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        GpioController controller = GpioFactory.getInstance();
        controller.shutdown();
        iledPin = controller.provisionDigitalOutputPin(getPin(), PinState.LOW);
        try {
            mcp3008GpioProvider = new MCP3008GpioProvider(SpiChannel.getByNumber(getChannelId()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create MCP3008GpioProvider", e);
        }

        analogPin = resolveAnalogPin();
        if (analogPin == null) {
            throw new RuntimeException("Did not find pin with address " + analogAddress);
        }
    }

    private Pin resolveAnalogPin() {
        for(Pin pin : MCP3008Pin.ALL) {
            if (pin.getAddress() == analogAddress) return pin;
        }
        return null;
    }

    /**
     * Hack to retrieve the correct Pin from RaspiPin.class lib
     *
     * @return the correct Pin
     */
    private Pin getPin() {

        if (LOG.isDebugEnabled()) {
            LOG.debug(" Pin Id > " + iled);
        }

        Pin ret = getPinPerFieldName();

        if (ret == null) {
            ret = getPinPerPinAddress();
            if (ret == null) {
                ret = getPinPerPinName();
            }
        }

        if (ret == null) {
            throw new IllegalArgumentException("Cannot find gpio [" + this.iled + "] ");
        }

        return ret;
    }

    private Pin getPinPerFieldName() {
        Pin ret = null;

        try {
            Field field = gpioClass.getDeclaredField(this.iled);
            ret = (Pin) field.get(null);
        } catch (NoSuchFieldException e) {
            LOG.trace(" Field " + iled + " not found in class " + gpioClass);
        } catch (SecurityException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        return ret;
    }

    private Pin getPinPerPinAddress() {
        Pin ret = null;

        int address = -1;

        try {
            address = Integer.parseInt(this.iled);
        } catch (Exception e) {
            LOG.trace(" gpioId " + iled + " not an address");
            return ret;
        }

        for (Field field : gpioClass.getFields()) {
            if (field.getType().equals(Pin.class)) {
                try {
                    ret = (Pin) field.get(null);
                    if (ret.getAddress() == address) {
                        return ret;
                    }
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }
        LOG.trace(" Address " + iled + " not found in class " + gpioClass);
        return ret;
    }

    private Pin getPinPerPinName() {
        Pin ret = null;

        for (Field field : gpioClass.getFields()) {
            if (field.getType().equals(Pin.class)) {
                try {
                    ret = (Pin) field.get(null);
                    if (ret.getName().compareTo(iled) == 0) {
                        return ret;
                    }
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }

        return ret;
    }

    @Override
    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("GP2Y1010AU0F component supports only consumer endpoints.");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        Gp2y1010au0fConsumer gp2y1010au0fConsumer = new Gp2y1010au0fConsumer(this, processor);
        if (!getConsumerProperties().containsKey("delay")) {
            gp2y1010au0fConsumer.setDelay(5000);
        }
        configureConsumer(gp2y1010au0fConsumer);
        return gp2y1010au0fConsumer;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Getters & Setters


    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getIled() {
        return iled;
    }

    public void setIled(String iled) {
        this.iled = iled;
    }

    public int getAnalogAddress() {
        return analogAddress;
    }

    public void setAnalogAddress(int analogAddress) {
        this.analogAddress = analogAddress;
    }

    public GpioPinDigitalOutput getIledPin() {
        return iledPin;
    }

    public void setIledPin(GpioPinDigitalOutput iledPin) {
        this.iledPin = iledPin;
    }

    public MCP3008GpioProvider getMcp3008GpioProvider() {
        return mcp3008GpioProvider;
    }

    public void setMcp3008GpioProvider(MCP3008GpioProvider mcp3008GpioProvider) {
        this.mcp3008GpioProvider = mcp3008GpioProvider;
    }

    public Pin getAnalogPin() {
        return this.analogPin;
    }

    public void setAnalogPin(Pin analogPin) {
        this.analogPin = analogPin;
    }

    public int getSamplingDelay() {
        return samplingDelay;
    }

    public void setSamplingDelay(int samplingDelay) {
        this.samplingDelay = samplingDelay;
    }
}
