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
package io.rhiot.deployer.detector;

import io.rhiot.deployer.detector.SimplePortScanningDeviceDetector;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.List;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class RpiDetectorTest extends Assert {

    SimplePortScanningDeviceDetector detector = new SimplePortScanningDeviceDetector();

    @Test
    public void shouldReachDevice() throws IOException {
        // Given
        List<Inet4Address> addresses = detector.detectReachableAddresses();
        assumeFalse("This test should be executed only when the proper network interfaces are available.",
                addresses.isEmpty());

        // When
        Inet4Address address = addresses.get(0);

        // Then
        assertTrue(address.isReachable(5000));
    }

    @Test
    public void shouldNotReachPiDevice() {
        List addresses = detector.detectDevices();
        assumeTrue("This test should be executed only when there is not Raspberry Pi device connected to the network.",
                addresses.isEmpty());

        assertEquals(0, addresses.size());
    }

}