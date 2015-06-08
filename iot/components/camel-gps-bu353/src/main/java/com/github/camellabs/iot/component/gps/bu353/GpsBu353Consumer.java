/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.component.gps.bu353;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultConsumer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

public class GpsBu353Consumer extends DefaultConsumer implements Runnable {

    private InputStream source;

    private ExecutorService executorService;

    public GpsBu353Consumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        source = getEndpoint().getGpsCoordinatesSource().source();

        executorService = getEndpoint().getCamelContext().getExecutorServiceManager().newSingleThreadExecutor(this, getEndpoint().getEndpointUri());
        executorService.execute(this);
    }

    @Override
    protected void doStop() throws Exception {
        if (executorService != null) {
            getEndpoint().getCamelContext().getExecutorServiceManager().shutdownNow(executorService);
            executorService = null;
        }
        source.close();
        super.doStop();
    }

    @Override
    public GpsBu353Endpoint getEndpoint() {
        return (GpsBu353Endpoint) super.getEndpoint();
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                GpsCoordinates coordinates = GpsCoordinates.parse(line);
                Exchange exchange = ExchangeBuilder.anExchange(getEndpoint().getCamelContext()).withBody(coordinates).build();
                getProcessor().process(exchange);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            getExceptionHandler().handleException(e);
        }
    }

}