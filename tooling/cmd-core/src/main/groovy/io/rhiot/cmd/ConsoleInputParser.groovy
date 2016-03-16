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

import static java.util.Optional.empty
import static java.util.Optional.of

class ConsoleInputParser {

    // Members

    private final String[] args

    // Constructors

    ConsoleInputParser(String... args) {
        this.args = args
    }

    boolean isHelp() {
        (args.contains('--help') || args.contains('-h') || args.find{!it.startsWith('-')} == null) && !args.find{ ['device-send', 'kura-install'].contains(it) }
    }

    String helpText() {
        """Welcome to the Rhiot command line tool.

Usage: rhiot [OPTIONS] command

Commands:
shell-start                     Starts background shell process.
device-config                   Edits configuration file on a device.
device-scan                     Lists possible target devices.
device-send                     Transfers file to a remote device.
kura-config-bootdelegation      Enables OSGi boot delegation for Sun packages on a remote device.
kura-config-ini                 Sets property on a /opt/eclipse/kura/kura/config.ini file on a remote device.
kura-install-felix-fileinstall  Deploys Felix File Install into your Kura server.
raspbian-install                Installs Raspbian to a target SD card.
deploy-gateway                  Deploys gateway to a detected device.

Global options:
 -h --help                                                      Prints this help page.
 -d --debug                                                     Debug (verbose) mode.

===

Command: shell-start

Example: shell-start

Starts background shell process (if one is not started already). You can connect to a shell process using SSH and
default credentials:

    ssh rhiot@localhost -p 2000
    password: rhiot

===

Command: device-config file property value

Example: device-config /etc/config delay 1000

Sets property on a given configuration file on a remote device. Configuration file will be created if doesn't exists.

SSH options:
--host (-ho) host    Address of the target device. The device will be automatically detected if this option is not specified.
--port (-p)          Port of the SSH server on a target device. Defaults to 22.
--username (-u)      SSH username of the device. Defaults to 'root'.
--password (-pa)     SSH password of the device. Defaults to 'raspberry'.

Other options:
--append (-a)         Appends to the given property instead of overriding it.

===

Command: device-scan

Example: device-scan

Lists possible target devices. Performs simple TCP port scanning and SSH authentication attempt under the hood.

===

Command: kura-config-bootdelegation

Example: kura-config-bootdelegation

Enables OSGi boot delegation for Sun packages i.e. adds `org.osgi.framework.bootdelegation=sun.*,com.sun.*` property to
the `/opt/eclipse/kura/kura/config.ini` file on a remote device. This settings is required to use libraries relying on
`com.sun` and `sun` packages deployed into Kura.

All the device-config command options are also available for this command.

===

Command: kura-config-ini property value

Example: kura-config-ini equinox.ds.debug true

Sets property on a /opt/eclipse/kura/kura/config.ini file on a remote device. All the device-config command options are also available
for this command.

===

Command: kura-install-felix-fileinstall

Example: kura-install-felix-fileinstall

Deploys Felix File Install into your Kura server. After Felix File Install is installed into your server, you can drop
bundle jars into `/opt/eclipse/kura/deploy` in order to deploy those.

All the device-config command options are also available for this command.

===

Command: raspbian-install sdDevice

Example: rhiot raspbian-install sdd1

===

Command: raspbian-config-boot property value

Example: raspbian-config-boot hdmi_drive 2

Sets property on a /boot/config.txt file on a remote device. All the device-config command options are also available
for this command.

===

Command: deploy-gateway

Example: rhiot deploy-gateway

Options:
 -a=group:artifact:version --artifact=group:artifact:version    Overrides default gateway artifact coordinates (io.rhiot:rhiot-gateway-app).
 -Pfoo=bar                                                      Adds foo=bar configuration property to the deployed gateway. Can be used multiple times.
 -u=user --username=user                                        SSH username of the target device.
 -p=secret --password=secret                                    SSH password of the target device."""
    }

    boolean isDebug() {
        args.contains('--debug') || args.contains('-d')
    }

    String command() {
        args.find{!it.startsWith('-')}
    }

    Map<String, String> properties() {
        args.findAll { it.startsWith('-P') }.inject([:]) { props, propertyArgument ->
            def propertyWithoutPrefix = propertyArgument.substring(2)
            def separatorIndex = propertyWithoutPrefix.indexOf('=')
            def propertyKey = propertyWithoutPrefix.substring(0, separatorIndex)
            def propertyValue = propertyWithoutPrefix.substring(separatorIndex + 1)
            props[propertyKey] = propertyValue
            props
        }
    }

    boolean hasCredentials() {
        def hasUsername = args.find{ it.startsWith('--username=')} || args.find{ it.startsWith('-u=')}
        def hasPassword = args.find{ it.startsWith('--password=')} || args.find{ it.startsWith('-p=')}
        if(hasUsername ^ hasPassword) {
            throw new ConsoleInformation('Both username and password must be specified.')
        }
        hasUsername && hasPassword
    }

    Optional<String> username() {
        option('username', 'u')
    }

    Optional<String> password() {
        option('password', 'p')
    }

    Optional<String> artifact() {
        option('artifact', 'a')
    }

    // Helpers

    private Optional<String> option(String longOption, String shortOption) {
        def option = args.find{ it.startsWith("--${longOption}=") || it.startsWith("-${shortOption}=") }
        option != null ? of(option.substring(option.indexOf('=') + 1)) : empty()
    }

}