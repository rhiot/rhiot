/**
 * Licensed to the Camel Labs under one or more
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
package io.rhiot.cloudlets

import com.example.MockHttpExchangeInterceptor
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import io.rhiot.cloudlets.device.DeviceCloudlet
import org.apache.commons.io.IOUtils
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6
import static io.rhiot.steroids.Steroids.APPLICATION_PACKAGE_PROPERTY
import static io.rhiot.utils.Networks.findAvailableTcpPort
import static io.rhiot.utils.Properties.setStringProperty

class CustomHttpExchangeInterceptorTest extends Assert {

    static def int mongodbPort = findAvailableTcpPort()

    @BeforeClass
    static void beforeClass() {
        System.setProperty('MONGODB_SERVICE_PORT', "${mongodbPort}")
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.V3_1_0)
                .net(new Net(mongodbPort, localhostIsIPv6()))
                .build();
        MongodStarter.getDefaultInstance().prepare(mongodConfig).start()

        setStringProperty(APPLICATION_PACKAGE_PROPERTY, 'com.example')
        new DeviceCloudlet().start().waitFor()
    }

    @Test
    void should() {
        IOUtils.toString(new URI("http://localhost:15000/device"))
        assertThat(MockHttpExchangeInterceptor.hasBeenCalled).isTrue()
    }

}
