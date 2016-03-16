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
package io.rhiot.cmd.commands

import io.rhiot.cmd.Command
import io.rhiot.cmd.OutputAppender
import io.rhiot.scanner.DeviceDetector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeviceScanCommand implements Command {

    private final DeviceDetector deviceDetector

    @Autowired
    DeviceScanCommand(DeviceDetector deviceDetector) {
        this.deviceDetector = deviceDetector
    }

    @Override
    String command() {
        'device-scan'
    }

    @Override
    void execute(OutputAppender appender, String... command) {
        appender.append('Scanning local networks for devices...')
        appender.append('')
        appender.append('======================================')
        appender.append("Device type\t\tIPv4 address")
        appender.append('--------------------------------------')
        deviceDetector.detectDevices().each {
            appender.append("${it.type()}\t\t${it.address()}")
        }
    }

}