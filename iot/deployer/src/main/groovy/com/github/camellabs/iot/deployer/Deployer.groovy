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
package com.github.camellabs.iot.deployer

import com.github.camellabs.iot.deployer.device.Device
import com.github.camellabs.iot.deployer.device.DeviceDetector
import com.github.camellabs.iot.utils.ssh.client.SshClient
import groovy.transform.Immutable
import org.apache.commons.lang3.SystemUtils

import java.nio.file.Paths

@Immutable
class Deployer {

    private def deviceDetector = new DeviceDetector()

    Device deploy(Map<String, String> additionalProperties) {
        println('Detecting devices...')
        def supportedDevices = deviceDetector.detectDevices()
        if (supportedDevices.isEmpty()) {
            throw new IllegalStateException('No supported devices detected.')
        }
        if (supportedDevices.size() > 1) {
            throw new IllegalStateException("More than one device detected: ${supportedDevices.size()}")
        }
        def device = supportedDevices.first()
        println("Detected Raspberry Pi at ${device.address().hostAddress}")

        def gatewayJar = Paths.get(SystemUtils.userHome.absolutePath, '.m2', 'repository',
                'com', 'github', 'camel-labs', 'camel-labs-iot-gateway', '0.1.1-SNAPSHOT',
                'camel-labs-iot-gateway-0.1.1-SNAPSHOT.jar').toFile()

        def ssh = new SshClient(device.address().hostAddress, 'pi', 'raspberry')
        def gatewayHome = '/var/camel-labs-iot-gateway'

        ssh.printCommand('sudo /etc/init.d/camel-labs-iot-gateway stop')

        println("Preparing gateway home directory ($gatewayHome)...")
        ssh.printCommand("sudo mkdir -p ${gatewayHome}")
        ssh.printCommand("sudo chown pi ${gatewayHome}")

        println("Cleaning old artifacts from gateway home directory ($gatewayHome)...")
        ssh.printCommand("sudo rm ${gatewayHome}/camel-labs-iot-gateway-*.jar")
        ssh.scp(new FileInputStream(gatewayJar), new File("${gatewayHome}/camel-labs-iot-gateway-0.1.1-SNAPSHOT.jar"), false)

        ssh.printCommand("sudo chown pi /etc/init.d")
        ssh.scp(getClass().getResourceAsStream('/camel-labs-iot-gateway.initd.sh'), new File('/etc/init.d/camel-labs-iot-gateway'), false)

        ssh.printCommand("sudo chown pi /etc/default")
        def p = new Properties()
        p.load(getClass().getResourceAsStream('/camel-labs-iot-gateway.config.sh'))
        p.putAll(additionalProperties)

        def output = new ByteArrayOutputStream()
        def pw = new PrintWriter(output)
        p.each { pw.println('export ' + it.key + '=' + it.value) }
        pw.flush()
        ssh.scp(new ByteArrayInputStream(output.toByteArray()), new File('/etc/default/camel-labs-iot-gateway'), false)

        ssh.printCommand('sudo chmod +x /etc/init.d/camel-labs-iot-gateway')
        ssh.printCommand('sudo update-rc.d camel-labs-iot-gateway defaults')

        ssh.printCommand('sudo /etc/init.d/camel-labs-iot-gateway start')

        device
    }

    Device deploy() {
        deploy([:])
    }

    public static void main(String[] args) {
        new Deployer().deploy()
    }

}
