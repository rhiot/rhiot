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
import io.rhiot.utils.ssh.client.SshClient
import org.springframework.stereotype.Component

import static java.lang.Boolean.parseBoolean

@Component
class DeviceConfigCommand extends SshCommandSupport {

    @Override
    protected List<String> doExecute(List<String> output, String... command) {
        super.doExecute(output, command)

        String file = requiredParameter('file', command[4])
        String property = command[5]
        String value = command[6]
        boolean append = parseBoolean(command[7])

        def properties = new Properties()
        def originalFile = sshClient.scp(new File(file))
        if(originalFile != null) {
            properties.load(new ByteArrayInputStream(originalFile))
        }

        boolean shouldAppend = append && properties.getProperty(property)
        if(shouldAppend) {
            properties.put(property, properties.get(property) + value)
        } else {
            properties.put(property, value)
        }
        def result = new ByteArrayOutputStream()
        def pw = new PrintWriter(result)
        properties.each { pw.println("${it.key} = ${it.value}") }
        pw.flush()
        result.close()

        sshClient.scp(new ByteArrayInputStream(result.toByteArray()), new File(file))

        if(shouldAppend) {
            output << "Appended '${value}' to property '${property}' in file ${file}."
        } else {
            output << "Updated ${file} - set property '${property}' to value '${value}'."
        }
        output
    }

}