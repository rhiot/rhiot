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

package io.rhiot.component.deviceio.gpio;

import io.rhiot.component.deviceio.DeviceIOConstants;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;

/**
 * Represents a DeviceIO GPIO Endpoint.
 */
@UriEndpoint(scheme = "deviceio", syntax = "deviceio-gpio://gpioId", consumerClass = GPIOConsumer.class, label = "iot", title = "RaspberryPi")
public class GPIOEndpoint extends DefaultEndpoint {

    private static final transient Logger LOG = LoggerFactory.getLogger(GPIOEndpoint.class);

    @UriPath(description = "pin gpioId")
    @Metadata(required = "true")
    private String gpioId;

    @UriParam(description = "Default : use Body if Action for ouput Pin (TOGGLE, HIGH, LOW)", enums = "TOGGLE:HIGH:LOW")
    private GPIOAction action;

    @UriParam(defaultValue = "false")
    private boolean state;

    @UriParam(defaultValue = "false")
    private boolean shutdownState;

    @UriParam(defaultValue = "0")
    private long delay;

    @UriParam(defaultValue = "50")
    private long duration = 50;

    @UriParam(defaultValue = "DIR_OUTPUT_ONLY(Producer),DIR_INPUT_ONLY(Consumer)", description = "")
    private String direction;

    @UriParam(defaultValue = "MODE_OUTPUT_PUSH_PULL(Producer),MODE_INPUT_PULL_UP(Consumer)", description = "")
    private String mode;

    @UriParam(defaultValue = "TRIGGER_BOTH_EDGES(Producer),TRIGGER_NONE(Consumer)", description = "")
    private String trigger;

    public GPIOEndpoint(String uri, String pin, GPIOComponent component) {
        super(uri, component);
    }

    /**
     * Create consumer map to an Input PIN
     */
    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        LOG.debug(this.toString());

        if (direction == null || (direction.compareTo("") == 0)) {
            direction = "DIR_INPUT_ONLY";
        }
        if (mode == null || (mode.compareTo("") == 0)) {
            mode = "MODE_INPUT_PULL_UP";
        }
        if (trigger == null || (trigger.compareTo("") == 0)) {
            trigger = "TRIGGER_BOTH_EDGES";
        }

        int internalDirection = internalValueWithOR(direction);
        int internalMode = internalValueWithOR(mode);
        int internalTrigger = internalValueWithOR(trigger);

        GPIOPinConfig pinConfig = new GPIOPinConfig(0, Integer.parseInt(this.gpioId), internalDirection, internalMode,
                internalTrigger, state);

        GPIOPin pin = DeviceManager.open(GPIOPin.class, pinConfig);
        return new GPIOConsumer(this, processor, pin);
    }

    /**
     * Create producer map to an Output PIN
     */
    @Override
    public Producer createProducer() throws Exception {
        LOG.debug(this.toString());

        if (direction == null || (direction.compareTo("") == 0)) {
            direction = "DIR_OUTPUT_ONLY";
        }
        if (mode == null || (mode.compareTo("") == 0)) {
            mode = "MODE_OUTPUT_PUSH_PULL";
        }
        if (trigger == null || (trigger.compareTo("") == 0)) {
            trigger = "TRIGGER_NONE";
        }

        int internalDirection = internalValueWithOR(direction);
        int internalMode = internalValueWithOR(mode);
        int internalTrigger = internalValueWithOR(trigger);

        GPIOPinConfig pinConfig = new GPIOPinConfig(DeviceConfig.DEFAULT, Integer.parseInt(this.gpioId),
                internalDirection, internalMode, internalTrigger, state);
        GPIOPin pin = DeviceManager.open(GPIOPin.class, pinConfig);

        return new GPIOProducer(this, pin);
    }

    public GPIOAction getAction() {
        return action;
    }

    public long getDelay() {
        return delay;
    }

    public String getDirection() {
        return direction;
    }

    public long getDuration() {
        return duration;
    }

    public String getGpioId() {
        return gpioId;
    }

    public String getMode() {
        return mode;
    }

    public String getTrigger() {
        return trigger;
    }

    private int internalValueWithOR(String valueList) {
        int ret = 0;

        Class<GPIOPinConfig> clazz = GPIOPinConfig.class;

        try {

            Pattern p = Pattern.compile(DeviceIOConstants.CAMEL_SPLIT_REGEX);
            Matcher m = p.matcher(valueList);

            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "Attr = " + valueList + " doesn't mach [" + DeviceIOConstants.CAMEL_SPLIT_REGEX + "]");
            }
            String[] directionList = valueList.split(DeviceIOConstants.CAMEL_SPLIT);

            for (String string : directionList) {
                Field clazzField = clazz.getField(string);
                ret |= (int) clazzField.get(null);
            }

        } catch (NoSuchFieldException e) {

            throw new IllegalArgumentException(e);

        } catch (SecurityException e) {
            LOG.debug("", e);
        } catch (IllegalArgumentException e) {
            LOG.debug("", e);
        } catch (IllegalAccessException e) {
            LOG.debug("", e);
        }

        return ret;
    }

    public boolean isShutdownState() {
        return shutdownState;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public boolean isState() {
        return state;
    }

    public void setAction(GPIOAction action) {
        this.action = action;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setGpioId(String gpioId) {
        this.gpioId = gpioId;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setShutdownState(boolean shutdownState) {
        this.shutdownState = shutdownState;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

}
