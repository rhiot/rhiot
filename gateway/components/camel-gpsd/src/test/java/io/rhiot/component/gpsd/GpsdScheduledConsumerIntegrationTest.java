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

import io.rhiot.scanner.Device;
import io.rhiot.scanner.DeviceDetector;
import io.rhiot.scanner.SimplePortScanningDeviceDetector;
import io.rhiot.utils.Networks;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.parseBoolean;
import static java.lang.System.getenv;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class GpsdScheduledConsumerIntegrationTest extends CamelTestSupport {

    static boolean isRpiAvailable;
    static DeviceDetector deviceDetector = new SimplePortScanningDeviceDetector();
    static String piAddress;
    static List<Device> devices;

    @BeforeClass
    public static void beforeClass() {
        assumeFalse(parseBoolean(getenv("IS_TRAVIS")));


        devices = deviceDetector.detectDevices();
        piAddress = devices.size() == 1 ? devices.get(0).address().getHostAddress() : null;
        isRpiAvailable = devices.size() == 1 && devices.get(0).type().equals(Device.DEVICE_RASPBERRY_PI_2) &&
                Networks.available(piAddress, GpsdConstants.DEFAULT_PORT);

        assumeTrue(isRpiAvailable);
    }

    @AfterClass
    public static void afterClass() {
        deviceDetector.close();
    }

    
    @Test
    public void testGpsdScheduledConsumer() throws Exception {
        
        MockEndpoint mock = getMockEndpoint("mock:foo");
        mock.expectedMinimumMessageCount(5);
        
        //Should get at least 5 messages within 30 seconds
        assertMockEndpointsSatisfied(30, TimeUnit.SECONDS);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("gpsd://gps?scheduled=true&consumer.delay=5000&host=" + piAddress).to("mock:foo");
            }
        };
    }
}
