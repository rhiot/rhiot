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
package io.rhiot.deployer

import io.rhiot.cmd.ConsoleInformation
import io.rhiot.cmd.DeployerBuilder
import io.rhiot.scanner.DeviceDetector
import org.junit.Assert
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static org.mockito.Mockito.mock

class CmdTest extends Assert {

    def deviceDetector = mock(DeviceDetector.class)

    def deployer = new DeployerBuilder().deviceDetector(deviceDetector).debug(true).build()

    @Test
    void shouldDetectNoSupportedDevices() {
        try {
            deployer.deploy()
        } catch (ConsoleInformation info) {
            assertThat(info.message).contains('No supported devices detected')
            return
        }
        fail()
    }

    @Test
    void shouldDownloadGivenGatewayArtifact() {
        def artifact =  deployer.gatewayArtifact(Optional.of('io.rhiot:rhiot-gateway-app:0.1.5-SNAPSHOT')).get()
        assertThat(artifact.available()).isGreaterThan(0)
    }

}
