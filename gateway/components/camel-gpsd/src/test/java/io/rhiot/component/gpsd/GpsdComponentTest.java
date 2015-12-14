/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rhiot.component.gpsd;

import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.types.PollObject;
import de.taimos.gpsd4java.types.TPVObject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.spi.ExceptionHandler;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GpsdComponentTest extends CamelTestSupport {

    private static final CountDownLatch LATCH = new CountDownLatch(1);

    GPSdEndpoint gpsd = mock(GPSdEndpoint.class);
    GPSdEndpoint failingGpsd = mock(GPSdEndpoint.class);

    @Before
    public void before() throws IOException {
        PollObject pollObject = new PollObject();
        pollObject.setFixes(singletonList(new TPVObject()));
        given(gpsd.poll()).willReturn(pollObject);
        given(failingGpsd.poll()).willThrow(new IllegalArgumentException("Forced"));
    }

    @Test
    public void smokeTest() throws Exception {
        Thread.sleep(2000);
    }
    
    @Test
    public void testException() throws Exception{

        getMockEndpoint("mock:result").expectedMessageCount(0);

        assertMockEndpointsSatisfied();

        assertTrue("Should have been called", LATCH.await(5, TimeUnit.SECONDS));
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("gpsd4javaEndpoint", gpsd);
        registry.bind("failingGpsdEndpoint", failingGpsd);
        registry.bind("myHandler", new MyExceptionHandler());
        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("gpsd://gps?scheduled=true&restartGpsd=false&gpsd4javaEndpoint=#gpsd4javaEndpoint").to("seda:mock");

                //Set router up for failure
                from("gpsd:failingGps?scheduled=true&restartGpsd=false&gpsd4javaEndpoint=#failingGpsdEndpoint&consumer.exceptionHandler=#myHandler").
                        routeId("gps").to("mock:result");
            }
        };
    }

    private final class MyExceptionHandler implements ExceptionHandler {

        @Override
        public void handleException(Throwable exception) {
            LATCH.countDown();
        }

        @Override
        public void handleException(String message, Throwable exception) {
            LATCH.countDown();
        }

        @Override
        public void handleException(String message, Exchange exchange, Throwable exception) {
            LATCH.countDown();
        }
    }

}
