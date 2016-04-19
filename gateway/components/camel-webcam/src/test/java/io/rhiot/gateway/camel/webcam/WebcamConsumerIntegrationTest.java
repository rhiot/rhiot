/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
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

import java.util.concurrent.TimeoutException;

import static io.rhiot.gateway.camel.webcam.WebcamHelper.closeWebcam;
import static io.rhiot.gateway.camel.webcam.WebcamHelper.isWebcamPresent;
import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getenv;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class WebcamConsumerIntegrationTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:scheduled")
    MockEndpoint mock;

    @EndpointInject(uri = "mock:jpg")
    MockEndpoint mjpgMock;
    
    @BeforeClass
    public static void before(){
        assumeFalse(parseBoolean(getenv("IS_TRAVIS")));
        assumeTrue(isWebcamPresent());
    }

    @AfterClass
    public static void after() throws TimeoutException {
        if(!parseBoolean(getenv("IS_TRAVIS")))
            WebcamHelper.closeWebcam();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("webcam:cam?consumer.delay=100").to("mock:scheduled");

                from("webcam:jpgCam?format=jpg").to("mock:jpg");
            }
        };
    }

    // Tests

    @Test
    public void testWebcamScheduledConsumer() throws Exception {
        mock.expectedMinimumMessageCount(3);
        mock.assertIsSatisfied();
    }

    @Test
    public void shouldReadMjpg() throws Exception {
        mjpgMock.expectedMinimumMessageCount(1);
        mjpgMock.assertIsSatisfied();
    }

}