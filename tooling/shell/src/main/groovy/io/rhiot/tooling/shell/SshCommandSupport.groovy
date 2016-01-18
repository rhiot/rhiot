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
import org.springframework.util.Assert

abstract class SshCommandSupport extends CommandSupport {

    @Autowired
    protected DeviceDetector deviceDetector

    // Base SSH arguments members

    protected String deviceAddress

    protected int port

    protected username

    protected password

    protected SshClient sshClient

    // Overridden

    @Override
    protected List<String> doExecute(List<String> output, String... command) {
        Assert.notNull(output)
        Assert.notNull(command)

        log().debug('About to execute command: {}', command.toList())

        deviceAddress = parameter(command[0]) {
            def devices = deviceDetector.detectDevices()
            if(devices.isEmpty()) {
                throw new IllegalStateException('No supported device detected.')
            } else if(devices.size() > 1) {
                throw new IllegalStateException("Expected single device. Found ${devices.size()}.")
            } else {
                devices.first().address().hostAddress
            }
        }
        port =  parameter('SSH port', command[1], 22)
        username = parameter(command[2], 'root')
        password = parameter(command[3], 'raspberry')
        sshClient = new SshClient(deviceAddress, port, username, password)
        output
    }

}