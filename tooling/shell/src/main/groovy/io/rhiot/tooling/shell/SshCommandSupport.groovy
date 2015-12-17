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
package io.rhiot.tooling.shell

import io.rhiot.scanner.DeviceDetector
import io.rhiot.utils.ssh.client.SshClient
import org.springframework.beans.factory.annotation.Autowired

import static org.apache.commons.lang3.StringUtils.isBlank

abstract class SshCommandSupport extends CommandSupport  {

    @Autowired
    protected DeviceDetector deviceDetector

    protected String deviceAddress

    protected int port

    protected username

    protected password

    protected SshClient sshClient

    @Override
    protected void doExecute(List<String> output, String... command) {
        deviceAddress = parameter(command[0]) {
            deviceDetector.detectDevices().first().address().hostAddress
        }
        String portString = command[1]
        port = isBlank(portString) ? 22 : portString.toInteger()
        username = parameter(command[2], 'root')
        password = parameter(command[3], 'raspberry')
        sshClient = new SshClient(deviceAddress, port, username, password)
    }

}