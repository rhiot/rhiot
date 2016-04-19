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

import io.rhiot.utils.process.EchoMockProcessManager
import io.rhiot.utils.process.ProcessManager
import io.rhiot.utils.ssh.client.SshClient
import io.rhiot.utils.ssh.server.SshServerBuilder
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import java.nio.file.Paths

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setIntProperty
import static io.rhiot.utils.Uuids.uuid
import static java.lang.Boolean.parseBoolean
import static java.lang.System.getenv
import static org.junit.Assume.assumeFalse

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Shell.class)
@Configuration
class ShellTest {

    // Fixtures

    static device = new SshServerBuilder().build().start()

    static int shellPort = findAvailableTcpPort()

    static def shellClient = new SshClient('localhost', shellPort, 'rhiot', 'rhiot')

    def file = uuid()

    @BeforeClass
    static void beforeClass() {
        setIntProperty('shell.ssh.port', shellPort)
    }

    @Bean
    ProcessManager processManager() {
        new EchoMockProcessManager()
    }

    // Commands core tests

    @Test
    void shouldExecuteCommand() {
        def result = shellClient.command('shell-start')
        assertThat(result.size()).isGreaterThan(1)
        assertThat(result.first()).contains('up and running')
    }

    @Test
    void shouldPrintSingleLineOfOutput() {
        def result = shellClient.command("device-config --host localhost --port ${device.port()} /${file} foo bar")
        assertThat(result).hasSize(1)
    }

    // device-config tests

    @Test
    void shouldAddConfigurationFile() {
        // When
        def result = configCommand("device-config","/${file} foo bar")

        // Then
        def property = device.config(file).getProperty('foo')
        assertThat(property).isEqualTo('bar')
        assertThat(result.first()).startsWith('Updated')
    }

    @Test
    void shouldOverrideProperty() {
        // Given
        configCommand('device-config', "/${file} foo bar")

        // When
        configCommand('device-config', "/${file} foo baz")

        // Then
        def property = device.config(file).getProperty('foo')
        assertThat(property).isEqualTo('baz')
    }

    @Test
    void shouldAppendProperty() {
        // Given
        configCommand('device-config', "/${file} foo bar")

        // When
        configCommand('device-config', "-a /${file} foo baz")
        def properties = new Properties()
        properties.load(new FileInputStream(new File(device.root(), file)))

        // Then
        assertThat(properties.getProperty('foo')).isEqualTo('barbaz')
    }

    @Test
    void shouldHandleMissingFile() {
        def result = shellClient.command("device-config --host localhost --port ${device.port()}")
        assertThat(result.first()).contains("Parameter \'file\' is required")
    }

    // device-send tests

    @Test
    void shouldDisplaySendFileHelp() {
        // When
        def result = configCommand("device-send", "--help")

        // Then
        assertThat(result).contains('Sends file to a remote device. For example:')
    }

    @Test
    void shouldSendFile() {
        // When
        def result = configCommand("device-send", "pom.xml /foo/${file}")

        // Then
        assertThat(Paths.get(device.root().absolutePath, 'foo', file).toFile().exists()).isTrue()
        assertThat(result.first()).startsWith('Sent')
    }

    @Test
    void shouldSendHttpFile() {
        // When
        def result = configCommand("device-send", "https://raw.githubusercontent.com/rhiot/rhiot/master/readme.md /foo/${file}")

        // Then
        assertThat(Paths.get(device.root().absolutePath, 'foo', file).toFile().exists()).isTrue()
        assertThat(result.first()).startsWith('Sent')
    }

    @Test
    void shouldSendMavenFile() {
        // When
        def result = configCommand("device-send", "mvn:com.google.guava/guava/18.0 /foo/${file}")

        // Then
        assertThat(Paths.get(device.root().absolutePath, 'foo', file).toFile().exists()).isTrue()
        assertThat(result.first()).startsWith('Sent')
    }

    // kura-config-bootdelegation tests

    @Test
    void shouldConfigureBootDelegation() {
        // When
        configCommand('kura-config-bootdelegation')

        // Then
        def properties = new Properties()
        properties.load(new FileInputStream(Paths.get(device.root().absolutePath, 'opt', 'eclipse', 'kura', 'kura', 'config.ini').toFile()))
        assertThat(properties.getProperty('org.osgi.framework.bootdelegation')).isEqualTo('sun.*,com.sun.*')
    }

    @Test
    void shouldNotFindDevicesForBootDelegationConfiguration() {
        assumeFalse(parseBoolean(getenv("IS_TRAVIS")));

        // When
        def output = shellClient.command("kura-config-bootdelegation")

        // Then
        assertThat(output.size()).isEqualTo(1)
        assertThat(output.first()).contains('No supported device detected')
    }

    // kura-config-ini tests

    @Test
    void shouldEditKuraConfigIni() {
        // When
        configCommand('kura-config-ini', 'foo bar')

        // Then
        sleep(5000) // Verify if that makes test more stable
        def properties = new Properties()
        properties.load(new FileInputStream(Paths.get(device.root().absolutePath, 'opt', 'eclipse', 'kura', 'kura', 'config.ini').toFile()))
        assertThat(properties.getProperty('foo')).isEqualTo('bar')
    }

    // kura-install-felix-fileinstall

    @Test
    void shouldInstallFelixFileinstallIntoKura() {
        // When
        configCommand('kura-install-felix-fileinstall')

        // Then
        assertThat(Paths.get(device.root().absolutePath, 'opt', 'eclipse', 'kura', 'plugins', 'org.apache.felix.fileinstall-3.5.0.jar').toFile().exists()).isTrue()
        def properties = new Properties()
        properties.load(new FileInputStream(Paths.get(device.root().absolutePath, 'opt', 'eclipse', 'kura', 'kura', 'config.ini').toFile()))
        assertThat(properties.getProperty('osgi.bundles')).contains('org.apache.felix.fileinstall-3.5.0.jar')
    }

    // raspbian-config-boot tests

    @Test
    void shouldPropertyToRaspianBootConfig() {
        // When
        configCommand('raspbian-config-boot', 'foo bar')

        // Then
        sleep(5000) // Verify if that makes test more stable
        def properties = new Properties()
        properties.load(new FileInputStream(Paths.get(device.root().absolutePath, 'boot', 'config.txt').toFile()))
        assertThat(properties.getProperty('foo')).isEqualTo('bar')
    }

    // Helpers

    def configCommand(String command, String remaining) {
        shellClient.command("${command} --host localhost --port ${device.port()} ${remaining}")
    }

    def configCommand(String command) {
        configCommand(command, '')
    }

}
