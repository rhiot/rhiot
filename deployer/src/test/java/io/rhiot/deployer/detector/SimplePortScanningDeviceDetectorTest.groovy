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
package io.rhiot.deployer.detector

import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class SimplePortScanningDeviceDetectorTest {

    def deviceDetector = new SimplePortScanningDeviceDetector(new StubInterfacesProvider())

    @Test
    void shouldNotDetectAnyReachableAddress() {
        def addresses = deviceDetector.detectReachableAddresses()
        assertThat(addresses).hasSize(0)
    }

}

class StubInterfacesProvider implements InterfacesProvider {

    @Override
    List<NetworkInterface> interfaces() {
        [new NetworkInterface(ipv4Address: '192.169.1.1', broadcast: '192.169.1.1'),
         new NetworkInterface(ipv4Address: '192.169.0.1', broadcast: '192.169.0.1')]
    }

}