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

import io.rhiot.tooling.devagent.commands.RaspbianInstallCommand
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory

class raspbian_install {

    @Usage("raspbian-install sdDevice")
    @Command
    def main(InvocationContext context, @Usage("Name of a device. For example 'sdd1'.") @Required @Argument String device) {
        BeanFactory beanFactory = context.attributes['spring.beanfactory']
        beanFactory.getBean(RaspbianInstallCommand.class).execute(device).join('\n')
    }

}