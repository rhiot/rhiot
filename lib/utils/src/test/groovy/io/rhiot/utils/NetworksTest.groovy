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
package io.rhiot.utils

import org.junit.Assert
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Networks.isReachable
import static io.rhiot.utils.Networks.currentLocalNetworkIp
import static java.lang.System.currentTimeMillis
import static java.util.concurrent.TimeUnit.SECONDS
import static org.junit.Assume.assumeTrue;

class NetworksTest extends Assert {

    @Test
    void shouldReturnAvailablePort() {
        assertThat(findAvailableTcpPort()).isGreaterThan(Networks.MIN_PORT_NUMBER)
    }

    @Test
    void shouldReachHost() {
        assumeTrue('This test should be executed only if you can access rhiot.io from your network.',
                isReachable('rhiot.io', (int) SECONDS.toMillis(10)))
    }

    @Test
    void shouldNotReachHost() {
        assertThat(isReachable("someUnreachableHostName${currentTimeMillis()}")).isFalse()
    }

    @Test
    void shouldReturnCurrentLocalNetworkIp() {
        assertThat(currentLocalNetworkIp()).isNotNull()
    }

}
