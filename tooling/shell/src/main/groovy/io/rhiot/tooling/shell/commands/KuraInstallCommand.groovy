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

import io.rhiot.cmd.DownloadManager
import io.rhiot.tooling.shell.SshCommandSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class KuraInstallCommand extends SshCommandSupport {

    private final DownloadManager downloadManager

    @Autowired
    KuraInstallCommand(DownloadManager downloadManager) {
        this.downloadManager = downloadManager
    }

    @Override
    protected List<String> doExecute(List<String> output, String... command) {
        output = super.doExecute(output, command)

        downloadManager.download(new URL('https://s3.amazonaws.com/kura_downloads/raspbian/release/1.3.0/kura_1.3.0_raspberry-pi-2_installer.deb'), 'kura_1.3.0_raspberry-pi-2_installer.deb')
        sshClient.scp(new FileInputStream(downloadManager.downloadedFile('kura_1.3.0_raspberry-pi-2_installer.deb')), new File('/opt/kura_1.3.0_raspberry-pi-2_installer.deb'))

        output += sshClient.command('sudo apt-get -qq update')
        output += sshClient.command('sudo apt-get -qq install hostapd bind9 iw monit dos2unix telnet')
        output += sshClient.command('dpkg -i /opt/kura_1.3.0_raspberry-pi-2_installer.deb')

        output << "Kura server has been installed on a device ${deviceAddress}."
        log().debug('Output collected: {}', output)
        output
    }

    @Override
    protected String printHelp() {
        """Command: kura-install

Example: kura-install

Installs Kura Server (1.3.0 with Web UI) into your target device, together with all dependant packages needed.

All the `device-config` command options are also available for this command."""
    }

}