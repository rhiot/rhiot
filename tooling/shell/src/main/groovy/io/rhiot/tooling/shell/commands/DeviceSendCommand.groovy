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
import io.rhiot.utils.ssh.client.SshClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static org.apache.commons.lang3.StringUtils.isBlank

@Component
class DeviceSendCommand extends CommandSupport {

    private final DeviceDetector deviceDetector

    @Autowired
    DeviceSendCommand(DeviceDetector deviceDetector) {
        this.deviceDetector = deviceDetector
    }

    @Override
    protected void doExecute(List<String> output, String... command) {
        def deviceAddress = parameter(command[0]){
            deviceDetector.detectDevices().first().address().hostAddress
        }
        String portString = command[1]
        int port = isBlank(portString) ? 22 : portString.toInteger()
        def username = parameter(command[2], 'root')
        def password = parameter(command[3], 'raspberry')
        def source = requiredParameter('source', command[4])
        def target = requiredParameter('target', command[5])

        def sshClient = new SshClient(deviceAddress, port, username, password)
        sshClient.scp(new FileInputStream(source), new File(target))

        output << "Sent ${source} to ${target} on a target device ${deviceAddress}."
    }

}