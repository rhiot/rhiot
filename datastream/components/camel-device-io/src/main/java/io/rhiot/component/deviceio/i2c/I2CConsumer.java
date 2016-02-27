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
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

/**
 * The I2C consumer.
 */
public class I2CConsumer extends ScheduledPollConsumer {
    private final I2CDriver driver;

    public I2CConsumer(I2CEndpoint endpoint, Processor processor, I2CDriver driver) {
        super(endpoint, processor);
        this.driver = driver;
    }

    public void createBody(Exchange exchange) throws Exception {
        exchange.getIn().setBody(driver.get());
    }

    public I2CDriver getDevice() {
        return driver;
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = getEndpoint().createExchange();
        try {
            createBody(exchange);
            getProcessor().process(exchange);
            return 1; // number of messages polled
        } finally {
            // log exception if an exception occurred and was not handled
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
            }
        }
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
    public I2CEndpoint getEndpoint() {
        return (I2CEndpoint) super.getEndpoint();
    }

}
