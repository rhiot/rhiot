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
package io.rhiot.component.pi4j.i2c;

import io.rhiot.component.pi4j.Pi4jConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.util.EndpointHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

/**
 * Represents a I2C endpoint.
 */
@UriEndpoint(scheme = "pi4j-i2c", syntax = "pi4j-i2c://busId/deviceId", consumerClass = I2CConsumer.class, label = "iot", title = "i2c")
public class I2CEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(I2CEndpoint.class);

    @UriPath
    @Metadata(required = "true")
    private String name;

    @UriParam(defaultValue = "")
    private int busId;

    @UriParam(defaultValue = "")
    private int deviceId;

    @UriParam(defaultValue = "0x00")
    private int address = 0x00;

    @UriParam(defaultValue = "")
    private I2CReadAction readAction;

    @UriParam(defaultValue = "-1")
    private int size = -1;

    @UriParam(defaultValue = "-1")
    private int offset = -1;

    @UriParam(defaultValue = "-1")
    private int bufferSize = -1;

    @UriParam(defaultValue = "")
    private String driver;

    // DO NOT EXPORT IT OUTSIDE, NO GETTER, NO SETTER
    private transient Class driverClass;

    // DO NOT EXPORT IT OUTSIDE, NO GETTER, NO SETTER
    private transient Map<String, Object> parameters;

    private transient I2CDevice device;

    private transient I2CBus bus;

    public I2CEndpoint(String uri, I2CComponent i2cComponent, String remaining, I2CBus bus,
            Map<String, Object> parameters) {
        super(uri, i2cComponent);

        this.bus = bus;
        this.parameters = parameters;

    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer ret = null;

        initDriver(I2CConsumer.class);

        device = bus.getDevice(deviceId);

        Constructor constructor = driverClass.getConstructor(I2CEndpoint.class, Processor.class, I2CDevice.class);

        ret = (Consumer) constructor.newInstance(this, processor, device);

        // Inject last parameter to i2c derived consumer
        EndpointHelper.setProperties(this.getCamelContext(), ret, parameters);

        if (!parameters.isEmpty()) {
            for (String param : parameters.keySet()) {
                LOG.warn(
                        "There are parameters that couldn't be set on the endpoint Consumer. Check the uri if the parameters are spelt correctly and that they are properties of the endpoint."
                                + " Unknown Consumer parameters=[" + param + "]");
            }
        }

        return ret;
    }

    @Override
    public Producer createProducer() throws Exception {
        Producer ret = null;

        initDriver(I2CProducer.class);

        device = bus.getDevice(deviceId);

        Constructor constructor = driverClass.getConstructor(I2CEndpoint.class, I2CDevice.class);

        ret = (Producer) constructor.newInstance(this, device);

        // Inject last parameter to i2c derived producer
        EndpointHelper.setProperties(this.getCamelContext(), ret, parameters);

        if (!parameters.isEmpty()) {
            for (String param : parameters.keySet()) {
                LOG.warn(
                        "There are parameters that couldn't be set on the endpoint Producer. Check the uri if the parameters are spelt correctly and that they are properties of the endpoint."
                                + " Unknown Producer parameters=[" + param + "]");
            }
        }
        return ret;
    }

    public int getAddress() {
        return address;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public I2CBus getBus() {
        return bus;
    }

    public int getBusId() {
        return busId;
    }

    public I2CDevice getDevice() {
        return device;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getDriver() {
        return driver;
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }

    public I2CReadAction getReadAction() {
        return readAction;
    }

    public int getSize() {
        return size;
    }

    private void initDriver(Class defaultClass) throws ClassNotFoundException, IOException {
        Class ret = null;

        // Force via driver
        if (driver != null && driver.compareTo("") != 0) {
            InputStream is = I2CEndpoint.class.getResourceAsStream(Pi4jConstants.CAMEL_I2C_DRIVER_LOCATION + driver);
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    if (!line.contains("#")) {
                        sb.append(line);
                    }
                }
            } finally {
                if (br != null) {
                    br.close();
                }
            }
            driverClass = I2CEndpoint.class.forName(sb.toString());
        }
        // Force default
        if (driverClass == null) {
            driverClass = defaultClass;
        }

    }

    @Override
    public boolean isLenientProperties() {
        return Pi4jConstants.CAMEL_PI4j_LENIENT;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setBus(I2CBus bus) {
        this.bus = bus;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public void setDevice(I2CDevice device) {
        this.device = device;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setDriver(String driverName) {
        this.driver = driverName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setReadAction(I2CReadAction readAction) {
        this.readAction = readAction;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
