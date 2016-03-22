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
import io.rhiot.cmd.ConsoleInputParser

import static com.google.common.truth.Truth.assertThat

import org.junit.Assert
import org.junit.Test

class ConsoleInputParserTest extends Assert {

    @Test
    void shouldReturnHelpForNoCommand() {
        assertThat(new ConsoleInputParser('-someOption=foo').help).isEqualTo(true)
    }

    @Test
    void shouldReturnScanCommandFromBeginning() {
        assertThat(new ConsoleInputParser('device-scan', '-a=foo:bar:1').command()).isEqualTo('device-scan')
    }

    @Test
    void shouldReturnScanCommandFromEnd() {
        assertThat(new ConsoleInputParser('-a=foo:bar:1', 'device-scan').command()).isEqualTo('device-scan')
    }

    @Test
    void shouldValidateUsernameWithoutPassword() {
        try {
            new ConsoleInputParser('--username=foo').hasCredentials()
        } catch (ConsoleInformation info) {
            assertTrue(info.message.contains('Both username and password must be specified'))
            return
        }
        fail()
    }

    @Test
    void shouldValidateUsernameAndPassword() {
        assertThat(new ConsoleInputParser('--username=foo', '--password=bar').hasCredentials()).isTrue()
    }

    @Test
    void shouldValidateNoUsernameAndPassword() {
        assertThat(new ConsoleInputParser().hasCredentials()).isFalse()
    }

    @Test
    void shouldParseUsername() {
        assertThat(new ConsoleInputParser('--username=foo').username().get()).isEqualTo('foo')
    }

    @Test
    void shouldParseGatewayArtifact() {
        assertThat(new ConsoleInputParser('--artifact=foo:bar:1').artifact().get()).isEqualTo('foo:bar:1')
    }

    @Test
    void shouldParseGatewayArtifactShort() {
        assertThat(new ConsoleInputParser('-a=foo:bar:1').artifact().get()).isEqualTo('foo:bar:1')
    }

    @Test
    void shouldParseEmptyGatewayArtifact() {
        assertThat(new ConsoleInputParser('--someRandomOption').artifact().isPresent()).isFalse()
    }
	
	@Test
	void shouldDebug() {
		assertThat(new ConsoleInputParser("-d","device-scan").isDebug()).isTrue()
	}
}
