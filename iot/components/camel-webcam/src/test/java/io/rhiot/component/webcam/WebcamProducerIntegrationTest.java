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
import org.junit.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

import static org.junit.Assume.assumeTrue;

public class WebcamProducerIntegrationTest extends CamelTestSupport {

    private static int width = 640;
    private static int height = 480;

    @BeforeClass
    public static void before(){
        assumeTrue(WebcamHelper.isWebcamPresent());
    }

    @Test
    public void testWebcamProducer() throws Exception {
        
        MockEndpoint mock = getMockEndpoint("mock:foo");
        mock.expectedMinimumMessageCount(1);
        
        template.requestBody("direct:cam", "");
        
        assertMockEndpointsSatisfied(15, TimeUnit.SECONDS);
    }

    @Test
    public void testWebcamResolution() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:resolution");
        mock.expectedMinimumMessageCount(1);
        
        template.requestBody("direct:resolution", "");
        
        assertMockEndpointsSatisfied(15, TimeUnit.SECONDS);
        byte[] body = mock.getExchanges().get(0).getIn().getBody(byte[].class);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(body));
        assertNotNull(bufferedImage);
        assertEquals(width, bufferedImage.getWidth());
        assertEquals(height, bufferedImage.getHeight());
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:resolution").to("webcam:cam?width=" + width + "&height=" + height).to("mock:resolution");
                from("direct:cam").to("webcam:cam").to("mock:foo");
            }
        };
    }
}
