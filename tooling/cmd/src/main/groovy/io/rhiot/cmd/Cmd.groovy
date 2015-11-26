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

import io.rhiot.deployer.Command
import io.rhiot.deployer.Deployer
import io.rhiot.bootstrap.Bootstrap

class Cmd {

    private final Bootstrap bootstrap

    private final List<Command> commands

    Cmd() {
        this.bootstrap = new Bootstrap().start()
        this.commands = bootstrap.beanRegistry().beans(Command.class)
    }

    void stop() {
        bootstrap.stop()
    }

    public static void main(String... args) {
        def cmd = new Cmd()
        try {
            Deployer.main(args)
        } finally {
            cmd.stop()
        }
    }

}
