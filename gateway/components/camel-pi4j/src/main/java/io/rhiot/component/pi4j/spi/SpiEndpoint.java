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
package io.rhiot.component.pi4j.spi;

import com.pi4j.io.spi.SpiDevice;
import io.rhiot.component.pi4j.Pi4jConstants;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Map;

@UriEndpoint(scheme = "pi4j-spi", syntax = "pi4j-spi://channelId", consumerClass = SpiConsumer.class, label = "iot", title = "spi")
public class SpiEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(SpiEndpoint.class);

    @UriPath
    @Metadata(required = "true")
    private String name;

    @UriParam(defaultValue = "")
    private int channelId;

    @UriParam(defaultValue = "")
    private String driver;

    // DO NOT EXPORT IT OUTSIDE, NO GETTER, NO SETTER
    private Class driverClass;

    // DO NOT EXPORT IT OUTSIDE, NO GETTER, NO SETTER
    private Map<String, Object> parameters;

    private SpiDevice spiDevice;

    public SpiEndpoint(String uri, SpiComponent spiComponent, String remaining, SpiDevice spiDevice,
                       Map<String, Object> parameters) {
        super(uri, spiComponent);

        this.spiDevice = spiDevice;
        this.parameters = parameters;

    }


    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer ret = null;

        initDriver(SpiConsumer.class);

        Constructor constructor = driverClass.getConstructor(SpiEndpoint.class, Processor.class, SpiDevice.class);

        ret = (Consumer) constructor.newInstance(this, processor, spiDevice);

        // Inject last parameter to spi derived consumer
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

        initDriver(SpiProducer.class);

        Constructor constructor = driverClass.getConstructor(SpiEndpoint.class, SpiDevice.class);

        ret = (Producer) constructor.newInstance(this, spiDevice);

        // Inject last parameter to spi derived producer
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

    private void initDriver(Class defaultClass) throws ClassNotFoundException, IOException {
        Class ret = null;

        // Force via driver
        if (driver != null && driver.compareTo("") != 0) {
            InputStream is = SpiEndpoint.class.getResourceAsStream(Pi4jConstants.CAMEL_SPI_DRIVER_LOCATION + driver);
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
            driverClass = SpiEndpoint.class.forName(sb.toString());
        }
        // Force default
        if (driverClass == null) {
            driverClass = defaultClass;
        }

    }

    // Getters & Setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public SpiDevice getSpiDevice() {
        return spiDevice;
    }

    public void setSpiDevice(SpiDevice spiDevice) {
        this.spiDevice = spiDevice;
    }
}
