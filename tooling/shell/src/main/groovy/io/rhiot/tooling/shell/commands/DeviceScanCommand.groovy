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
package io.rhiot.tooling.shell.commands

import io.rhiot.scanner.DeviceDetector
import io.rhiot.tooling.shell.CommandSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeviceScanCommand extends CommandSupport {

    private final DeviceDetector deviceDetector

    @Autowired
    DeviceScanCommand(DeviceDetector deviceDetector) {
        this.deviceDetector = deviceDetector
    }

    @Override
    protected void doExecute(List<String> output, String... command) {
        output << 'Scanning local networks for devices...'
        output << ''
        output << '======================================'
        output << "Device type\t\tIPv4 address"
        output << '--------------------------------------'
        deviceDetector.detectDevices().each {
            output << "${it.type()}\t\t${it.address()}"
        }
    }

}