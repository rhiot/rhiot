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
package io.rhiot.gateway.camel.webcam;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getenv;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class WebcamMotionConsumerIntegrationTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:detected")
    MockEndpoint mockDetected;

    @BeforeClass
    public static void before(){
        assumeFalse(parseBoolean(getenv("IS_TRAVIS")));
        assumeTrue(WebcamHelper.isWebcamPresent());
    }

    @AfterClass
    public static void after() throws TimeoutException {
        if(!parseBoolean(getenv("IS_TRAVIS")))
            WebcamHelper.closeWebcam();
    }
    
    @Test
    public void testMotion() throws InterruptedException {
        // Given
        mockDetected.setMinimumExpectedMessageCount(1);

        // When
        mockDetected.assertIsSatisfied();

        // Then
        assertNotNull(mockDetected.getExchanges().get(0).getIn().getHeader(WebcamConstants.WEBCAM_MOTION_EVENT_HEADER));
    }
    
    @Test
    public void testMotionUndetected() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:undetected");
        mock.setExpectedMessageCount(0);

        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("webcam:spycam?motion=true").to("mock:undetected");
                from("webcam:spycam?motion=true&pixelThreshold=0").to("mock:detected");
            }
        };
    }
}
