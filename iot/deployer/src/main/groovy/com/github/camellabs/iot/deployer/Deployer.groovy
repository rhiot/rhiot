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

import com.github.camellabs.iot.deployer.maven.JcabiMavenArtifactResolver
import com.github.camellabs.iot.utils.ssh.client.SshClient

class Deployer {

    private final DeviceDetector deviceDetector

    private final boolean debug

    Deployer(DeviceDetector deviceDetector, boolean debug) {
        this.deviceDetector = deviceDetector
        this.debug = debug
    }

    Deployer(boolean debug) {
        this(new SimplePortScanningDeviceDetector(), debug)
    }

    Deployer() {
        this(false)
    }

    Device deploy(Map<String, String> additionalProperties) {
        println('Downloading gateway binaries...')
        def gatewayJar = new JcabiMavenArtifactResolver().artifactStream('com.github.camel-labs', 'camel-labs-iot-gateway', '0.1.1-SNAPSHOT')

        println('Detecting devices...')
        def supportedDevices = deviceDetector.detectDevices()
        if (supportedDevices.isEmpty()) {
            throw new ConsoleInformation('No supported devices detected.')
        }
        if (supportedDevices.size() > 1) {
            throw new ConsoleInformation("More than one device detected: ${supportedDevices.size()}")
        }
        def device = supportedDevices.first()
        println("Detected Raspberry Pi at ${device.address().hostAddress}")

        def ssh = new SshClient(device.address().hostAddress, 'pi', 'raspberry')
        def gatewayHome = '/var/camel-labs-iot-gateway'

        ssh.printCommand('sudo /etc/init.d/camel-labs-iot-gateway stop')

        println("Preparing gateway home directory ($gatewayHome)...")
        ssh.printCommand("sudo mkdir -p ${gatewayHome}")
        ssh.printCommand("sudo chown pi ${gatewayHome}")

        println("Cleaning old artifacts from gateway home directory ($gatewayHome)...")
        ssh.printCommand("sudo rm ${gatewayHome}/camel-labs-iot-gateway-*.jar")
        ssh.scp(gatewayJar, new File("${gatewayHome}/camel-labs-iot-gateway-0.1.1-SNAPSHOT.jar"), false)

        ssh.printCommand("sudo chown pi /etc/init.d")
        ssh.scp(getClass().getResourceAsStream('/camel-labs-iot-gateway.initd.sh'), new File('/etc/init.d/camel-labs-iot-gateway'), false)

        ssh.printCommand("sudo chown pi /etc/default")
        def properties = new Properties()
        properties.load(getClass().getResourceAsStream('/camel-labs-iot-gateway.config.sh'))
        properties.putAll(additionalProperties)

        def output = new ByteArrayOutputStream()
        def pw = new PrintWriter(output)
        properties.each { pw.println('export ' + it.key + '=' + it.value) }
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

    // Main runner

    public static void main(String[] args) {
        def parser = new ConsoleInputParser(args)
        if(parser.help) {
            println(parser.helpText())
            return
        }

        try {
            new Deployer(parser.debug).deploy(parser.properties())
        } catch (Exception e) {
            if(!(e instanceof ConsoleInformation)) {
                print 'Error: '
            }
            println e.message
            if(parser.debug) {
                e.printStackTrace()
            }
        }
    }

}
