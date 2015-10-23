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

package io.rhiot.component.webcam;

import com.github.sarxos.webcam.Webcam;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assume.assumeTrue;

public class WebcamMotionConsumerIntegrationTest extends CamelTestSupport {

    @BeforeClass
    public static void before(){
        assumeTrue(WebcamHelper.isWebcamPresent());
    }
    
    @Test
    public void testMotion() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:detected");
        mock.setMinimumExpectedMessageCount(1);

        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
        
        assertNotNull(mock.getExchanges().get(0).getIn().getHeader(WebcamConstants.WEBCAM_MOTION_EVENT_HEADER));
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
