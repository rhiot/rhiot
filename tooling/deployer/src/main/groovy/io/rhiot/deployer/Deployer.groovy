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
package io.rhiot.deployer

import groovy.transform.PackageScope
import io.rhiot.deployer.detector.Device
import io.rhiot.deployer.detector.DeviceDetector
import io.rhiot.deployer.detector.SimplePortScanningDeviceDetector
import io.rhiot.deployer.maven.JcabiMavenArtifactResolver
import io.rhiot.utils.ssh.client.SshClient

import java.util.concurrent.Future

import static io.rhiot.utils.Mavens.MavenCoordinates.parseMavenCoordinates
import static io.rhiot.utils.Mavens.artifactVersionFromDependenciesProperties
import static java.util.Optional.empty

class Deployer {

    private final DeviceDetector deviceDetector

    private final boolean debug

    private final String username

    private final String password

    def JcabiMavenArtifactResolver artifactResolver = new JcabiMavenArtifactResolver()

    Deployer(DeviceDetector deviceDetector, String username, String password, boolean debug) {
        this.deviceDetector = deviceDetector
        this.username = username
        this.password = password
        this.debug = debug
    }

    def close() {
        deviceDetector.close()
        artifactResolver.close()
    }

    Device deploy(Optional<String> gatewayArtifactCoordinates, Map<String, String> additionalProperties) {
        def gatewayJar = gatewayArtifact(gatewayArtifactCoordinates)

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

        def ssh = new SshClient(device.address().hostAddress, username, password)
        def gatewayHome = '/var/camel-labs-iot-gateway'

        ssh.printCommand('sudo /etc/init.d/camel-labs-iot-gateway stop')

        println("Preparing gateway home directory ($gatewayHome)...")
        ssh.printCommand("sudo mkdir -p ${gatewayHome}")
        ssh.printCommand("sudo chown pi ${gatewayHome}")

        println("Cleaning old artifacts from gateway home directory ($gatewayHome)...")
        ssh.printCommand("sudo rm ${gatewayHome}/camel-labs-iot-gateway-*.jar")
        ssh.scp(gatewayJar.get(), new File("${gatewayHome}/camel-labs-iot-gateway-0.1.1-SNAPSHOT.jar"), false)

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
        deploy(empty(), [:])
    }

    // Helpers

    @PackageScope
    Future<InputStream> gatewayArtifact(Optional<String> gatewayArtifactCoordinates) {
        def coordinatesString = gatewayArtifactCoordinates.orElseGet{ "io.rhiot:rhiot-gateway-app:${artifactVersionFromDependenciesProperties('io.rhiot', 'rhiot-deployer')}"}
        def coordinates = parseMavenCoordinates(coordinatesString)
        artifactResolver.artifactStream(coordinates.groupId, coordinates.artifactId, coordinates.version)
    }

    // Main runner

    public static void main(String[] args) {
        def parser = new ConsoleInputParser(args)
        if (parser.help) {
            println(parser.helpText())
            return
        }

        try {
            switch(parser.command()) {
                case 'scan':
                    println 'Scanning local networks for devices...'
                    println()
                    println '======================================'
                    println "Device type\t\tIPv4 address"
                    println '--------------------------------------'
                    def detector = new SimplePortScanningDeviceDetector()
                    detector.detectDevices().each {
                        println "${it.type()}\t\t${it.address()}"
                    }
                    detector.close()
                    break;
                case 'deploy-gateway':
                    deployGateway(parser)
                    break;
            }
        } catch (Exception e) {
            if (!(e instanceof ConsoleInformation)) {
                print 'Error: '
            }
            println e.message
            if (parser.debug) {
                e.printStackTrace()
            }
        } finally {
            Runtime.getRuntime().exit(0)
        }
    }

    // Helpers

    @PackageScope
    static deployGateway(ConsoleInputParser parser) {
        def Deployer deployer = null
        try {
            def deployerBuilder = new DeployerBuilder()
            if (parser.hasCredentials()) {
                deployerBuilder.username(parser.username().get()).password(parser.password().get())
            }
            deployerBuilder.debug(parser.debug)
            deployer = deployerBuilder.build()
            deployer.deploy(parser.artifact(), parser.properties())
        } finally {
            if(deployer != null) {
                deployer.close()
            }
        }
    }

}