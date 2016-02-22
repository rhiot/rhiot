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

import io.rhiot.utils.ssh.client.SshClient
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory
import org.apache.sshd.server.PasswordAuthenticator
import org.apache.sshd.server.command.ScpCommandFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.sftp.SftpSubsystem

import java.nio.file.Paths

import static io.rhiot.utils.Networks.findAvailableTcpPort
import static java.io.File.createTempFile

class SshServer {

    private final PasswordAuthenticator authenticator

    private final int port

    private final File root

    // Constructors

    SshServer(PasswordAuthenticator authenticator, int port, File root) {
        this.authenticator = authenticator
        this.port = port
        this.root = root
    }

    // Life-cycle

    SshServer start() {
        def sshd = org.apache.sshd.SshServer.setUpDefaultServer()
        sshd.setPort(port)
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(createTempFile('rhiot', 'host_keys').absolutePath));
        sshd.setPasswordAuthenticator(authenticator)
        sshd.setCommandFactory(new ScpCommandFactory())
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(root.absolutePath))
        sshd.setSubsystemFactories([new SftpSubsystem.Factory()])
        sshd.start()

        this
    }

    // Factory methods

    SshClient client(String username, String password) {
        new SshClient('localhost', port, username, password)
    }

    Properties config(String path) {
        def properties = new Properties()
        def absolutePath = new File(root, path).absolutePath
        properties.load(new FileInputStream(Paths.get(absolutePath).toFile()))
        properties
    }

    // Accessors

    int port() {
        port
    }

    File root() {
        root
    }

}