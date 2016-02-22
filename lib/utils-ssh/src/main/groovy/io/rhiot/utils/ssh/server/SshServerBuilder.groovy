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
package io.rhiot.utils.ssh.server

import org.apache.sshd.server.PasswordAuthenticator

import static io.rhiot.utils.Networks.findAvailableTcpPort

class SshServerBuilder {

    private PasswordAuthenticator authenticator = new AnyCredentialsPasswordAuthenticator()

    private int port = findAvailableTcpPort()

    private File root = File.createTempDir()

    SshServer build() {
        new SshServer(authenticator, port, root)
    }

    SshServerBuilder port(int port) {
        this.port = port
        this
    }

    SshServerBuilder root(File root) {
        this.root = root
        this
    }

    SshServerBuilder authenticator(PasswordAuthenticator authenticator) {
        this.authenticator = authenticator
        this
    }

}
