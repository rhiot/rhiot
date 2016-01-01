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

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultScheduledPollConsumer;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Gp2y1010au0fConsumer extends DefaultScheduledPollConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(Gp2y1010au0fConsumer.class);
    private static final double MIN_VOLTAGE = 600.0;
    private static final double REF_VOLTAGE = 5000.0;

    public Gp2y1010au0fConsumer(DefaultEndpoint defaultEndpoint, Processor processor) {
        super(defaultEndpoint, processor);
    }

    @Override
    protected int poll() throws Exception {
        int iterations = 10;
        double sum = 0.0;

        for (int i = 0; i < iterations; i ++) {
            getEndpoint().getIledPin().high();
            TimeUnit.MICROSECONDS.sleep(getEndpoint().getSamplingDelay());
            double adcValue = getEndpoint().getMcp3008GpioProvider().getValue(getEndpoint().getAnalogPin());
            getEndpoint().getIledPin().low();

            double voltage = (REF_VOLTAGE / 1023.0) * adcValue * 11;

            if (voltage > MIN_VOLTAGE) {
                sum += (voltage - MIN_VOLTAGE) * 0.2;
            }

            TimeUnit.MILLISECONDS.sleep(50);
        }

        double average = sum / iterations;

        if (average > 0) {
            Exchange exchange = ExchangeBuilder
                    .anExchange(getEndpoint().getCamelContext())
                    .withBody(Precision.round(average, 2))
                    .build();
            exchange.setFromEndpoint(getEndpoint());
            getProcessor().process(exchange);
            return 1;
        }

        return 0;
    }

    @Override
    public Gp2y1010au0fEndpoint getEndpoint() {
        return (Gp2y1010au0fEndpoint) super.getEndpoint();
    }
}
