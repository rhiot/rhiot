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

import io.rhiot.tooling.shell.SshCommandSupport
import io.rhiot.utils.maven.MavenArtifactResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import static io.rhiot.utils.Mavens.MavenCoordinates.parseMavenCoordinates

@Component
class DeviceSendCommand extends SshCommandSupport {

    MavenArtifactResolver mavenArtifactResolver

    @Autowired
    DeviceSendCommand(MavenArtifactResolver mavenArtifactResolver) {
        this.mavenArtifactResolver = mavenArtifactResolver
    }

    @Override
    protected List<String> doExecute(List<String> output, String... command) {
        output = super.doExecute(output, command)
        def source = requiredParameter('source', command[4])
        def target = requiredParameter('target', command[5])

        log().debug('About to send {} to {}:{}.', source, deviceAddress, target)
        sshClient.scp(sourceStream(source), new File(target))
        log().debug('Sent {} to {}:{}.', source, deviceAddress, target)

        output << "Sent ${source} to ${target} on a target device ${deviceAddress}."
        log().debug('Output collected: {}', output)
        output
    }

    private sourceStream(String source){
        if(source.startsWith('mvn:')) {
            def coordinates = parseMavenCoordinates(source.substring(4), '/')
            mavenArtifactResolver.artifactStream(coordinates.groupId, coordinates.artifactId, coordinates.version).get()
        } else if(source =~ /.+:.+/) {
            new URL(source).openStream()
        } else {
            new FileInputStream(source)
        }
    }

    @Override
    protected String printHelp() {
        """Command: device-send source target

Sends file to a remote device. For example:

    device-send /etc/localfile /etc/remotefile

All the device-config command SSH options are also available for this command.

**Available since Rhiot 0.1.4.**

Using URL as a file source is accepted as well. For example to read file from HTTP and send it to a remote device:

    device-send http://example.com/file /etc/remotefile

You can also transfer Maven artifacts. For example to copy Google Guava jar to a remote device, execute the following
command:

    device-send mvn:com.google.guava/guava/18.0 /opt/guava.jar"""
    }

}