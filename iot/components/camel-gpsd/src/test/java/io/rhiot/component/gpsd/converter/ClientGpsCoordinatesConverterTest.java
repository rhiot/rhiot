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
package io.rhiot.component.gpsd.converter;

import io.rhiot.component.gpsd.ClientGpsCoordinates;
import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultClassResolver;
import org.apache.camel.impl.DefaultFactoryFinderResolver;
import org.apache.camel.impl.DefaultPackageScanClassResolver;
import org.apache.camel.impl.converter.DefaultTypeConverter;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.ReflectionInjector;
import org.apache.camel.util.ServiceHelper;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

public class ClientGpsCoordinatesConverterTest extends CamelTestSupport {

    private TypeConverter converter = new DefaultTypeConverter(new DefaultPackageScanClassResolver(),
            new ReflectionInjector(), new DefaultFactoryFinderResolver().resolveDefaultFactoryFinder(new DefaultClassResolver()));
    private LocalDateTime expectedDate = LocalDateTime.of(2015, 9, 1, 12, 00);
    private double lat = 23.23;
    private double lng = 12.12;
    private String expectedBody = "1441108800000" + "," + lat + "," + lng;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        ServiceHelper.startService(converter);
    }
    
    @Test
    public void testSerialization() throws Exception {
        assertEquals(expectedBody, converter.mandatoryConvertTo(String.class, new ClientGpsCoordinates(
                Date.from(expectedDate.toInstant(UTC)), lat, lng)));
    }

    @Test
    public void testDeserialization() throws Exception {
        ClientGpsCoordinates expected = new ClientGpsCoordinates(Date.from(expectedDate.toInstant(UTC)), lat, lng);
        ClientGpsCoordinates actual = converter.mandatoryConvertTo(ClientGpsCoordinates.class, expectedBody);
        assertTrue(expected.lat() == actual.lat());
        assertTrue(expected.lng() == actual.lng());
    }
}