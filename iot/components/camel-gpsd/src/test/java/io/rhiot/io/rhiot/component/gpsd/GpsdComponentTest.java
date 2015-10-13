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

package io.rhiot.io.rhiot.component.gpsd;

import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.types.PollObject;
import de.taimos.gpsd4java.types.TPVObject;
import io.rhiot.component.gpsd.GpsdConstants;
import io.rhiot.deployer.detector.Device;
import io.rhiot.deployer.detector.DeviceDetector;
import io.rhiot.deployer.detector.SimplePortScanningDeviceDetector;
import io.rhiot.utils.Networks;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.rhiot.deployer.detector.Device.DEVICE_RASPBERRY_PI_2;
import static io.rhiot.utils.Properties.booleanProperty;
import static java.util.Collections.singletonList;
import static org.junit.Assume.assumeTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

public class GpsdComponentTest extends CamelTestSupport {

    GPSdEndpoint gpsd = mock(GPSdEndpoint.class);

    @Before
    public void before() throws IOException {
        PollObject pollObject = new PollObject();
        pollObject.setFixes(singletonList(new TPVObject()));
        given(gpsd.poll()).willReturn(pollObject);
    }

    @Test
    public void smokeTest() throws Exception {
        Thread.sleep(2000);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("gpsd4javaEndpoint", gpsd);
        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("gpsd://gps?scheduled=true&restartGpsd=false&gpsd4javaEndpoint=#gpsd4javaEndpoint").to("seda:mock");
            }
        };
    }

}
