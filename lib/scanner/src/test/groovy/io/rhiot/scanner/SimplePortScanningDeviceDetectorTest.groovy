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
package io.rhiot.scanner

import io.rhiot.utils.ssh.server.NoneCredentialsPasswordAuthenticator
import io.rhiot.utils.ssh.server.SshServerBuilder;
import org.junit.Assert
import org.junit.Ignore;
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static java.lang.Boolean.parseBoolean
import static java.lang.System.getenv
import static org.junit.Assume.assumeFalse;

public class SimplePortScanningDeviceDetectorTest extends Assert {

    static sshd = new SshServerBuilder().build().start()

    def detector = new SimplePortScanningDeviceDetector();

    @Test
    void shouldNotReachDevice() {
        assumeFalse(parseBoolean(getenv('IS_TRAVIS')))
        def addresses = detector.detectDevices(findAvailableTcpPort())
        assertEquals(0, addresses.size());
    }

    @Ignore("See #572")
    @Test
    void shouldReachDevice() throws IOException {
        assumeFalse(parseBoolean(getenv('IS_TRAVIS')))

        def address = detector.detectDevices(sshd.port()).first()
        def ssh = new Socket(address.address(), sshd.port())
        assertThat(ssh.isConnected()).isTrue()
    }

    @Test
    void shouldNotFindRaspberryPi() {
        assumeFalse(parseBoolean(getenv('IS_TRAVIS')))
        def raspberryPi = new SshServerBuilder().
                authenticator(new NoneCredentialsPasswordAuthenticator()).
                build().start()
        def addresses = detector.detectDevices(raspberryPi.port())
        assertEquals(0, addresses.size());
    }

}