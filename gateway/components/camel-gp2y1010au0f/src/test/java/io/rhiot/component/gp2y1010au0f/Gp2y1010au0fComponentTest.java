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
package io.rhiot.component.gp2y1010au0f;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiFactory;
import io.rhiot.component.pi4j.mock.RaspiGpioProviderMock;
import io.rhiot.component.pi4j.mock.SpiDeviceMock;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;


@RunWith(PowerMockRunner.class)
public class Gp2y1010au0fComponentTest extends CamelTestSupport {

    public static final RaspiGpioProviderMock MOCK_RASPI = new RaspiGpioProviderMock();

    @BeforeClass
    public static void beforeClass() throws IOException {
        GpioFactory.setDefaultProvider(MOCK_RASPI);
    }

    @Test
    @PrepareForTest({ SpiFactory.class })
    public void consumerShouldReadSensorValue() throws IOException {
        PowerMockito.mockStatic(SpiFactory.class);
        PowerMockito.when(SpiFactory.getInstance(any(SpiChannel.class))).thenReturn(new SpiDeviceMock() {
            @Override
            public short[] write(short... shorts) throws IOException {
                return new short[] {0, 0, 27};
            }
        });

        double result = consumer.receiveBody("gp2y1010au0f://name", double.class);

        assertEquals(170.32, result, 0.0);
    }
}
