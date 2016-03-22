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
package io.rhiot.cloud.paas.bootstrap

import com.sun.management.OperatingSystemMXBean

import static io.rhiot.utils.Properties.longProperty
import static java.lang.management.ManagementFactory.operatingSystemMXBean

class Bootstrap {

    static void main(String... args) {
        println new Bootstrap().containersNumbers()
    }

    long containersNumbers() {
        longProperty('CONTAINERS_NUMBER').orElseGet {
            def system = (OperatingSystemMXBean) getOperatingSystemMXBean()
            system.totalPhysicalMemorySize / 1024 / 1024 / 1024 + 1
        }
    }

}