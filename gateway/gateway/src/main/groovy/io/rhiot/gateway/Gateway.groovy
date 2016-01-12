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
package io.rhiot.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import io.rhiot.cloudplatform.runtime.spring.CloudPlatform
import org.jolokia.jvmagent.JvmAgent

import static org.slf4j.LoggerFactory.getLogger

/**
 * IoT gateway boostrap. Starts Vert.x event bus, detects verticles and starts these.
 */
class Gateway extends CloudPlatform {

    private static final LOG = getLogger(Gateway.class)

    static final def JSON = new ObjectMapper()

    // Life-cycle

    Gateway start() {
        def gateway = super.start() as Gateway
        gateway
    }

    @Override
    Gateway stop() {
        super.stop() as Gateway
    }

    // Main method handler

    public static void main(String[] args) {
        JvmAgent.agentmain('host=0.0.0.0')
        new Gateway().start()
    }

}