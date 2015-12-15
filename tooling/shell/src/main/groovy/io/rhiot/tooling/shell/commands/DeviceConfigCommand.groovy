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
class DeviceConfigCommand extends CommandSupport {

    private final DeviceDetector deviceDetector

    @Autowired
    DeviceConfigCommand(DeviceDetector deviceDetector) {
        this.deviceDetector = deviceDetector
    }

    @Override
    protected void doExecute(List<String> output, String... command) {
        def deviceAddress = parameter(command[0]){
            deviceDetector.detectDevices().first().address().hostAddress
        }

        String portString = command[1]
        int port = isBlank(portString) ? 22 : portString.toInteger()

        String username = parameter(command[2], 'root')
        String password = parameter(command[3], 'raspberry')
        String file = requiredParameter('file', command[4])
        String property = command[5]
        String value = command[6]

        def sshClient = new SshClient(deviceAddress, port, username, password)

        def properties = new Properties()
        def originalFile = sshClient.scp(new File(file))
        if(originalFile != null) {
            properties.load(new ByteArrayInputStream(originalFile))
        }
        properties.put(property, value)
        def result = new ByteArrayOutputStream()
        properties.store(result, 'Updated by Rhiot')
        result.close()

        sshClient.scp(new ByteArrayInputStream(result.toByteArray()), new File(file))

        output << "Updated ${file} - set property '${property}' to value '${value}'."
    }

}