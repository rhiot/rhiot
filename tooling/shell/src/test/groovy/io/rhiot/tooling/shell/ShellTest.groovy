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
package io.rhiot.tooling.shell

import io.rhiot.utils.ssh.client.SshClient
import io.rhiot.utils.ssh.client.SshServer
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setIntProperty
import static io.rhiot.utils.Uuids.uuid

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Shell.class)
class ShellTest {

    static device = new SshServer().start()

    static int sshPort = findAvailableTcpPort()

    def file = uuid()

    @BeforeClass
    static void beforeClass() {
        setIntProperty('shell.ssh.port', sshPort)
    }

    @Test
    void shouldExecuteCommand() {
        def result = new SshClient('localhost', sshPort, 'rhiot', 'rhiot').command('shell-start')
        assertThat(result.size()).isGreaterThan(1)
        assertThat(result.first()).contains('up and running')
    }

    // device-config tests

    @Test
    void shouldAddConfigurationFile() {
        // When
        def result = new SshClient('localhost', sshPort, 'rhiot', 'rhiot').command("device-config --host localhost --port ${device.port()} /${file} foo bar")
        def properties = new Properties()
        properties.load(new FileInputStream(new File(device.root(), file)))

        // Then
        assertThat(properties.getProperty('foo')).isEqualTo('bar')
        assertThat(result.first()).startsWith('Updated')
    }

    @Test
    void shouldHandleMissingFile() {
        def result = new SshClient('localhost', sshPort, 'rhiot', 'rhiot').command("device-config --host localhost --port ${device.port()}")
        assertThat(result.first()).contains("Parameter \'file\' is required")
    }

}
