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
package io.rhiot.cmd

import static io.rhiot.utils.Mavens.artifactVersionFromDependenciesProperties
import static io.rhiot.utils.Mavens.MavenCoordinates.parseMavenCoordinates
import static java.util.Optional.empty
import groovy.transform.PackageScope
import io.rhiot.cmd.commands.RaspbianInstallCommand
import io.rhiot.scanner.Device
import io.rhiot.scanner.DeviceDetector
import io.rhiot.scanner.JavaNetInterfaceProvider
import io.rhiot.scanner.SimplePortScanningDeviceDetector
import io.rhiot.utils.maven.JcabiMavenArtifactResolver
import io.rhiot.utils.process.DefaultProcessManager
import io.rhiot.utils.process.ProcessManager
import io.rhiot.utils.ssh.client.SshClient

import java.util.concurrent.Future

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component;;

@SpringBootApplication
class Cmd {

    private final DeviceDetector deviceDetector

    private final boolean debug

    private final String username

    private final String password

    def JcabiMavenArtifactResolver artifactResolver = new JcabiMavenArtifactResolver()

    Cmd() { 
	}

    Cmd(DeviceDetector deviceDetector, String username, String password, boolean debug) {
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
        def gatewayHome = '/var/rhiot-gateway'

        ssh.printCommand('sudo /etc/init.d/rhiot-gateway stop')

        println("Preparing gateway home directory ($gatewayHome)...")
        ssh.printCommand("sudo mkdir -p ${gatewayHome}")
        ssh.printCommand("sudo chown pi ${gatewayHome}")

        println("Cleaning old artifacts from gateway home directory ($gatewayHome)...")
        ssh.printCommand("sudo rm ${gatewayHome}/rhiot-gateway-*.jar")
        ssh.scp(gatewayJar.get(), new File("${gatewayHome}/rhiot-gateway-0.1.1-SNAPSHOT.jar"))

        ssh.printCommand("sudo chown pi /etc/init.d")
        ssh.scp(getClass().getResourceAsStream('/rhiot-gateway.initd.sh'), new File('/etc/init.d/rhiot-gateway'))

        ssh.printCommand("sudo chown pi /etc/default")
        def properties = new Properties()
        properties.load(getClass().getResourceAsStream('/rhiot-gateway.config.sh'))
        properties.putAll(additionalProperties)

        def output = new ByteArrayOutputStream()
        def pw = new PrintWriter(output)
        properties.each { pw.println('export ' + it.key + '=' + it.value) }
        pw.flush()
        ssh.scp(new ByteArrayInputStream(output.toByteArray()), new File('/etc/default/rhiot-gateway'))

        ssh.printCommand('sudo chmod +x /etc/init.d/rhiot-gateway')
        ssh.printCommand('sudo update-rc.d rhiot-gateway defaults')

        ssh.printCommand('sudo /etc/init.d/rhiot-gateway start')

        device
    }

    Device deploy() {
        deploy(empty(), [:])
    }

    // Helpers

    @PackageScope
    Future<InputStream> gatewayArtifact(Optional<String> gatewayArtifactCoordinates) {
        def coordinatesString = gatewayArtifactCoordinates.orElseGet{ "io.rhiot:rhiot-gateway-app:${artifactVersionFromDependenciesProperties('io.rhiot', 'rhiot-tooling-cmd-core')}"}
        def coordinates = parseMavenCoordinates(coordinatesString)
        artifactResolver.artifactStream(coordinates.groupId, coordinates.artifactId, coordinates.version)
    }

    // Main runner

    public static void main(String[] args) {
        def app = new SpringApplication(Cmd.class)
		app.setAddCommandLineProperties(true);
        app.setBannerMode(Banner.Mode.OFF)
        def ctx = app.run(args)
        def commandsManager = ctx.getBean(CommandsManager.class)
		
		def parser = new ConsoleInputParser(args)
		if (parser.help) {
			println(parser.helpText())
			return
		}

        try {
            def command = parser.command()
            if(command == 'deploy-gateway') {
                deployGateway(parser)
            } else if(commandsManager.hasCommand(command)) {
                commandsManager.command(command).execute(new StdoutOutputAppender(), args.length == 0 ? new String[0] : ((String[]) Arrays.copyOfRange(args, 1, args.length)))
            } else {
                def output = new SshClient('localhost', 2000, 'rhiot', 'rhiot').command(args.join(' '))
                if(output.isEmpty()) {
                    throw new ConsoleInformation("No such command. Execute 'rhiot --help' to see all the available commands.")
                }
                println output.join('\n')
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
            ctx.close()
            Runtime.getRuntime().exit(0)
        }
    }

    // Helpers

    @PackageScope
    static deployGateway(ConsoleInputParser parser) {
        def Cmd deployer = null
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


    @Bean(destroyMethod = "close")
    DeviceDetector deviceDetector(ApplicationArguments arg) {
		String user = "";
		String pass = "";
		if(arg.containsOption("username")) {
			user = arg.getOptionValues("username").get(0)
		} else if(arg.containsOption("u")) {
			user = arg.getnOptionValues("u").get(0)
		}
		if(arg.containsOption("password")) {
			pass = arg.getOptionValues("password").get(0)
		}else if(arg.containsOption("pa")) {
			pass = arg.getOptionValues("pa").get(0)
		}
        
		new SimplePortScanningDeviceDetector(new JavaNetInterfaceProvider(), user, pass, 500)
    }

    @Bean
    DownloadManager downloadManager() {
        new DownloadManager()
    }

    @ConditionalOnMissingBean
    @Bean
    ProcessManager processManager() {
        new DefaultProcessManager()
    }

    // Commands

    @Bean
    RaspbianInstallCommand raspbianInstallCommand(@Value('${devices.directory:/dev}') String devicesDirectory,
                                                  DownloadManager downloadManager, ProcessManager processManager) {
        new RaspbianInstallCommand(devicesDirectory, downloadManager, processManager)
    }

}
