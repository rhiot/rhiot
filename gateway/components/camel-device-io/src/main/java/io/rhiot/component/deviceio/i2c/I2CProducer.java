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

import io.rhiot.component.deviceio.i2c.driver.I2CDriver;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * The I2C producer.
 */
public class I2CProducer extends DefaultProducer {

    protected I2CDriver driver;

    public I2CProducer(I2CEndpoint endpoint, I2CDriver driver) {
        super(endpoint);
        this.driver = driver;
    }

    @Override
    public I2CEndpoint getEndpoint() {
        return (I2CEndpoint) super.getEndpoint();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setBody(driver.get());
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        driver.start();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        driver.stop();
    }

    @Override
    protected void doShutdown() throws Exception {
        super.doShutdown();
        driver.shutdown();
    }

}
