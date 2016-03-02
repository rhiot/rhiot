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
package io.rhiot.component.deviceio.i2c;

import io.rhiot.component.deviceio.DeviceIOConstants;
import io.rhiot.component.deviceio.i2c.driver.I2CDriver;

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

import jdk.dio.DeviceManager;
import jdk.dio.i2cbus.I2CDevice;
import jdk.dio.i2cbus.I2CDeviceConfig;

/**
 * Represents a I2C endpoint.
 */
@UriEndpoint(scheme = "deviceio", syntax = "deviceio-i2c://busId/deviceId", consumerClass = I2CConsumer.class, label = "iot,dio,i2c", title = "deviceio-i2c")
public class I2CEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(I2CEndpoint.class);

    @UriPath
    @Metadata(required = "true")
    private String name;

    @UriParam(defaultValue = "")
    private int busId;

    @UriParam(defaultValue = "")
    private int deviceId;

    @UriParam(defaultValue = "")
    private String driver;

    // DO NOT EXPORT IT OUTSIDE, NO GETTER, NO SETTER
    private transient Class driverClass;

    // DO NOT EXPORT IT OUTSIDE, NO GETTER, NO SETTER
    private transient Map<String, Object> parameters;

    // DO NOT EXPORT IT OUTSIDE, NO GETTER, NO SETTER
    private transient I2CDevice device;

    public I2CEndpoint(String uri, I2CComponent i2cComponent, String remaining, Map<String, Object> parameters) {
        super(uri, i2cComponent);
        this.parameters = parameters;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        initDriver(I2CConsumer.class);

        device = DeviceManager.open(new I2CDeviceConfig(busId, deviceId,
                DeviceIOConstants.CAMEL_I2C_DIO_ADDRESS_SIZE_BITS, DeviceIOConstants.CAMEL_I2C_DIO_SERIAL_CLOCK));

        Constructor constructor = driverClass.getConstructor(I2CDevice.class);

        I2CDriver driver = (I2CDriver) constructor.newInstance(device);

        // Inject last parameter to i2c derived consumer
        EndpointHelper.setProperties(this.getCamelContext(), driver, parameters);

        if (!parameters.isEmpty()) {
            for (String param : parameters.keySet()) {
                LOG.warn(
                        "There are parameters that couldn't be set on the endpoint Consumer. Check the uri if the parameters are spelt correctly and that they are properties of the endpoint."
                                + " Unknown Consumer parameters=[" + param + "]");
            }
        }

        return new I2CConsumer(this, processor, driver);
    }

    @Override
    public Producer createProducer() throws Exception {

        initDriver(I2CDriver.class);

        device = DeviceManager.open(new I2CDeviceConfig(busId, deviceId,
                DeviceIOConstants.CAMEL_I2C_DIO_ADDRESS_SIZE_BITS, DeviceIOConstants.CAMEL_I2C_DIO_SERIAL_CLOCK));

        Constructor constructor = driverClass.getConstructor(I2CDevice.class);

        I2CDriver driver = (I2CDriver) constructor.newInstance(device);

        // Inject last parameter to i2c derived producer
        EndpointHelper.setProperties(this.getCamelContext(), driver, parameters);

        if (!parameters.isEmpty()) {
            for (String param : parameters.keySet()) {
                LOG.warn(
                        "There are parameters that couldn't be set on the endpoint Producer. Check the uri if the parameters are spelt correctly and that they are properties of the endpoint."
                                + " Unknown Producer parameters=[" + param + "]");
            }
        }
        return new I2CProducer(this, driver);
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

    private void initDriver(Class defaultClass) throws ClassNotFoundException, IOException {
        Class ret = null;

        // Force via driver
        if (driver != null && driver.compareTo("") != 0) {
            InputStream is = I2CEndpoint.class
                    .getResourceAsStream(DeviceIOConstants.CAMEL_I2C_DRIVER_LOCATION + driver);
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    if (!line.contains("#")) {
                        sb.append(line);
                        break;
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
        return DeviceIOConstants.CAMEL_I2C_DIO_LENIENT;
    }

    @Override
    public boolean isSingleton() {
        return true;
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

}
