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
package io.rhiot.cmd

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static com.google.common.truth.Truth.assertThat
import static java.lang.Boolean.parseBoolean
import static java.lang.System.getenv
import static org.junit.Assume.assumeFalse

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Cmd.class)
class CmdTest {

    @Autowired
    CommandsManager commandsManager

    @Test
    void shouldLoadDeviceScanCommand() {
        assertThat(commandsManager.hasCommand('device-scan')).isTrue()
    }

    @Test
    void shouldPerformScanning() {
        assumeFalse(parseBoolean(getenv("IS_TRAVIS")));
        def appender = new InMemoryOutputAppender()
        commandsManager.command('device-scan').execute(appender)
        assertThat(appender.output().join('')).startsWith('Scanning')
    }

    @Test
    void shouldLoadRaspbianInstallCommand() {
        assertThat(commandsManager.hasCommand('raspbian-install')).isTrue()
    }
	
}
