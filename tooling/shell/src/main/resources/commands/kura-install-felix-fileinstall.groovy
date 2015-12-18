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
package commands

import io.rhiot.tooling.shell.commands.DeviceConfigCommand
import io.rhiot.tooling.shell.commands.DeviceSendCommand
import org.crsh.cli.Command
import org.crsh.cli.Option
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory

import static io.rhiot.tooling.shell.CommandSupport.optionalBoolean

class kura_install_felix_fileinstall {

    @Usage("kura-install-felix-fileinstall")
    @Command
    def main(InvocationContext context,
             @Option(names = ['host', 'ho']) String host, @Option(names = ['port', 'p']) String port,
             @Option(names = ['username', 'u']) String username, @Option(names = ['password', 'pa']) String password) {
        BeanFactory beanFactory = context.attributes['spring.beanfactory']

        def targetPath = '/opt/eclipse/kura/plugins/org.apache.felix.fileinstall-3.5.0.jar'
        def out = [beanFactory.getBean(DeviceSendCommand.class).execute(host, port, username, password,
                'mvn:org.apache.felix/org.apache.felix.fileinstall/3.5.0', targetPath)]
        out += beanFactory.getBean(DeviceConfigCommand.class).execute(host, port, username, password,
                '/opt/eclipse/kura/kura/config.ini', 'osgi.bundles', ", reference\\:file\\:${targetPath}@1\\:start", optionalBoolean(true))
        out += beanFactory.getBean(DeviceConfigCommand.class).execute(host, port, username, password,
                '/opt/eclipse/kura/kura/config.ini', 'felix.fileinstall.dir', '/opt/eclipse/kura/deploy', optionalBoolean(false))
        out += beanFactory.getBean(DeviceConfigCommand.class).execute(host, port, username, password,
                '/opt/eclipse/kura/kura/config.ini', 'felix.fileinstall.noInitialDelay', 'true', optionalBoolean(false))
        out += beanFactory.getBean(DeviceConfigCommand.class).execute(host, port, username, password,
                '/opt/eclipse/kura/kura/config.ini', 'felix.fileinstall.start.level', '2', optionalBoolean(false))
        out.join('\n')
    }

}