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
package io.rhiot.component.pi4j.gpio;

import java.lang.reflect.Field;

import io.rhiot.component.pi4j.Pi4jConstants;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a GPIO endpoint.
 */
@UriEndpoint(scheme = "pi4j-gpio", syntax = "pi4j-gpio://gpioId", consumerClass = GPIOConsumer.class, label = "platform,iot", title = "RaspberryPi")
public class GPIOEndpoint extends DefaultEndpoint {

    private static final transient Logger LOG = LoggerFactory.getLogger(GPIOEndpoint.class);

    @UriPath(description = "pin gpioId (pi4j and wiringpi index)")
    @Metadata(required = "true")
    private String gpioId;

    @UriParam(description = "Digital Only: if input mode then state trigger event, if output then started value")
    private PinState state = null;

    @UriParam(defaultValue = "DIGITAL_OUTPUT", enums = "DIGITAL_OUTPUT:DIGITAL_INPUT:PWM_OUTPUT:DIGITAL_OUTPUT:ANALOG_OUTPUT")
    @Metadata(required = "true")
    private PinMode mode = PinMode.DIGITAL_OUTPUT;

    @UriParam(description = "Default : use Body if Action for ouput Pin (TOGGLE, BLINK, HIGH, LOW for digital only) (HEADER digital and analog)", enums = "TOGGLE:BLINK:HIGH:LOW:HEADER")
    private GPIOAction action;

    @UriParam(defaultValue = "0", description = "Analog or PWN Only")
    private double value;

    @UriParam(defaultValue = "true", description = "pin shutdown export")
    private boolean shutdownExport = true;

    @UriParam(defaultValue = "LOW", description = "pin state value before exit program")
    private PinState shutdownState = PinState.LOW;

    @UriParam(defaultValue = "OFF", description = "pin resistance before exit program")
    private PinPullResistance shutdownResistance = PinPullResistance.OFF;

    @UriParam(defaultValue = "PULL_UP")
    private PinPullResistance pullResistance = PinPullResistance.PULL_UP;

    @UriParam(defaultValue = "0")
    private long delay = 0;

    @UriParam(defaultValue = "50")
    private long duration = 50;

    @UriParam(defaultValue = Pi4jConstants.CAMEL_GPIO_CLAZZ)
    private Class gpioClass = RaspiPin.class;

    private GpioController controller;

    public GPIOEndpoint() {
        super();
    }

    public GPIOEndpoint(String uri, String pin, GPIOComponent component, GpioController crtl) {
        super(uri, component);
        ObjectHelper.notNull(crtl, "controller");
        this.controller = crtl;
    }

    /**
     * Create consumer map to an Input PIN
     */
    public Consumer createConsumer(Processor processor) throws Exception {
        LOG.debug(this.toString());

        ObjectHelper.notNull(this.mode, "mode");
        GpioPin pin = isAlreadyProvisioned();

        if (pin == null) {
            switch (this.mode) {
            case DIGITAL_INPUT:
                pin = getOrCreateController().provisionDigitalInputPin(getPin(), pullResistance);
                break;
            case ANALOG_INPUT:
                pin = getOrCreateController().provisionAnalogInputPin(getPin());
                break;
            case ANALOG_OUTPUT:
            case DIGITAL_OUTPUT: // PinMode.allOutput()
            case PWM_OUTPUT:
                LOG.error("Cannot create Consumer with OUTPUT Mode");
                return null;
            default:
                LOG.error("Cannot create Consumer w/o Mode");
                break;
            }

        } else {
            // enhancement we could manage several pins with one consumer
            throw new IllegalArgumentException("Cannot create twice same input pin [" + this.gpioId + "] for Consumer");
        }

        return new GPIOConsumer(this, processor, pin, state);
    }

    /**
     * Create producer map to an Output PIN
     */
    public Producer createProducer() throws Exception {
        LOG.debug(this.toString());
        ObjectHelper.notNull(this.mode, "mode");
        GpioPin pin = isAlreadyProvisioned();
        if (pin == null) {
            switch (this.mode) {
            case DIGITAL_OUTPUT:
                pin = getOrCreateController().provisionDigitalOutputPin(getPin(), state);
                break;
            case ANALOG_OUTPUT:
                pin = getOrCreateController().provisionAnalogOutputPin(getPin(), value);
                break;
            case PWM_OUTPUT:
                pin = getOrCreateController().provisionPwmOutputPin(getPin(), (int) value);
                break;
            case ANALOG_INPUT: // PinMode.allInput()
            case DIGITAL_INPUT:
                LOG.error("Cannot create Producer with INPUT Mode");
                return null;
            default:
                LOG.error("Cannot create Producer w/o Mode");
                break;
            }
            pin.setMode(this.mode); // Force Mode to avoid NPE
        } else { // enhancement we could manage several pins with one producer
            throw new IllegalArgumentException(
                    "Cannot create twice same output gpio [" + this.gpioId + "] for Producer");
        }

        // shutdownOption(pin);
        return new GPIOProducer(this, pin, action);
    }

    public GPIOAction getAction() {
        return action;
    }

    public GpioController getController() {
        return controller;
    }

    public long getDelay() {
        return delay;
    }

    public long getDuration() {
        return duration;
    }

    public Class getGpioClass() {
        return gpioClass;
    }

    public String getGpioId() {
        return gpioId;
    }

    public PinMode getMode() {
        return mode;
    }

    public GpioController getOrCreateController() {
        return controller;
    }

    /**
     * Hack to retrieve the correct Pin from RaspiPin.class lib
     * 
     * @return the correct Pin
     */
    private Pin getPin() {

        if (LOG.isDebugEnabled()) {
            LOG.debug(" Pin Id > " + gpioId);
        }

        Pin ret = getPinPerFieldName();

        if (ret == null) {
            ret = getPinPerPinAddress();
            if (ret == null) {
                ret = getPinPerPinName();
            }
        }

        if (ret == null) {
            throw new IllegalArgumentException("Cannot find gpio [" + this.gpioId + "] ");
        }

        return ret;
    }

    private Pin getPinPerFieldName() {
        Pin ret = null;

        try {
            Field field = gpioClass.getDeclaredField(this.gpioId);
            ret = (Pin) field.get(null);
        } catch (NoSuchFieldException e) {
            LOG.trace(" Field " + gpioId + " not found in class " + gpioClass);
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
            address = Integer.parseInt(this.gpioId);
        } catch (Exception e) {
            LOG.trace(" gpioId " + gpioId + " not an address");
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
        LOG.trace(" Address " + gpioId + " not found in class " + gpioClass);
        return ret;
    }

    private Pin getPinPerPinName() {
        Pin ret = null;

        for (Field field : gpioClass.getFields()) {
            if (field.getType().equals(Pin.class)) {
                try {
                    ret = (Pin) field.get(null);
                    if (ret.getName().compareTo(gpioId) == 0) {
                        return ret;
                    }
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }

        return ret;
    }

    public PinPullResistance getPullResistance() {
        return pullResistance;
    }

    public boolean getShutdownExport() {
        return shutdownExport;
    }

    public PinPullResistance getShutdownResistance() {
        return shutdownResistance;
    }

    public PinState getShutdownState() {
        return shutdownState;
    }

    public PinState getState() {
        return state;
    }

    public double getValue() {
        return value;
    }

    private GpioPin isAlreadyProvisioned() {
        GpioPin ret = null;

        for (GpioPin pin : getOrCreateController().getProvisionedPins()) {
            if (pin.getPin().getAddress() == Integer.parseInt(gpioId)) {
                ret = pin;
                break;
            }
        }
        return ret;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setAction(GPIOAction action) {
        this.action = action;
    }

    public void setController(GpioController controller) {
        this.controller = controller;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setGpioClass(Class gpioClass) {
        this.gpioClass = gpioClass;
    }

    public void setGpioId(String gpioId) {
        this.gpioId = gpioId;
    }

    public void setMode(PinMode mode) {
        this.mode = mode;
    }

    public void setPullResistance(PinPullResistance pullResistance) {
        this.pullResistance = pullResistance;
    }

    public void setShutdownExport(boolean shutdownExport) {
        this.shutdownExport = shutdownExport;
    }

    public void setShutdownResistance(PinPullResistance shutdownResistance) {
        this.shutdownResistance = shutdownResistance;
    }

    public void setShutdownState(PinState shutdownState) {
        this.shutdownState = shutdownState;
    }

    public void setState(PinState state) {
        this.state = state;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private void shutdownOption(GpioPin pin) {
        pin.setShutdownOptions(shutdownExport, shutdownState, shutdownResistance);
    }

}
