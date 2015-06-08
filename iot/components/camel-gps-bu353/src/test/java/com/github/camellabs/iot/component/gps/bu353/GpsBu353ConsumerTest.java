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

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class GpsBu353ConsumerTest extends CamelTestSupport {

    @Test
    public void shouldReadGpsCoordinates() {
        GpsCoordinates coordinates = consumer.receiveBody("gps-bu353://gps", GpsCoordinates.class);
        assertEquals(49.493202, coordinates.lng(), 0.01);
        assertEquals(19.032611, coordinates.lat(), 0.01);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("gpsCoordinatesSource", new MockGpsCoordinatesSource());
        return registry;
    }

}
