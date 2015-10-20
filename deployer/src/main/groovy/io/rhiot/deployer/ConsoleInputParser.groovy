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

import static java.util.Optional.empty
import static java.util.Optional.of

class ConsoleInputParser {

    // Constants

    static final def DEFAULT_COMMAND = 'deploy-gateway'

    // Members

    private final String[] args

    // Constructors

    ConsoleInputParser(String... args) {
        this.args = args
    }

    boolean isHelp() {
        args.contains('--help') || args.contains('-h')
    }

    String helpText() {
        """Welcome to the Rhiot Project Deployer application.

Usage: docker run -t rhiot/deployer [deploy-gateway|scan]

Commands:
deploy-gateway (default)    Deploys gateway to the device.
scan                        Lists possible target devices.

Options:
 -h --help                                                      Prints this help page.
 -d --debug                                                     Debug (verbose) mode.
 -a=group:artifact:version --artifact=group:artifact:version    Overrides default gateway artifact coordinates (io.rhiot:rhiot-gateway-app).
 -Pfoo=bar                                                      Adds foo=bar configuration property to the deployed gateway. Can be used multiple times.
 -u=user --username=user                                        SSH username of the target device.
 -p=secret --password=secret                                    SSH password of the target device."""
    }

    boolean isDebug() {
        args.contains('--debug') || args.contains('-d')
    }

    String command() {
        def command = args.find{!it.startsWith('-')} ?: DEFAULT_COMMAND
        if(![DEFAULT_COMMAND, 'scan'].contains(command)) {
            throw new IllegalArgumentException("No such command - ${command} .")
        }
        command
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