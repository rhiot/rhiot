package io.rhiot.tooling.shell.commands

import io.rhiot.scanner.DeviceDetector
import io.rhiot.utils.ssh.client.SshClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static org.apache.commons.lang3.StringUtils.isBlank

@Component
class DeviceConfigCommand {

    private final DeviceDetector deviceDetector

    @Autowired
    DeviceConfigCommand(DeviceDetector deviceDetector) {
        this.deviceDetector = deviceDetector
    }

    List<String> execute(String deviceAddress, Integer port, String username, String password, String file, String property, String value) {
        if(isBlank(deviceAddress)) {
            deviceAddress = deviceDetector.detectDevices().first().address().hostAddress
        }
        if(port == null) {
            port = 22
        }
        if(isBlank(username)) {
            username = 'root'
        }
        if(isBlank(password)) {
            password = 'raspberry'
        }

        def sshClient = new SshClient(deviceAddress, port, username, password)

        def properties = new Properties()
        def originalFile = sshClient.scp(new File(file))
        if(originalFile != null) {
            properties.load(new ByteArrayInputStream(originalFile))
        }
        properties.put(property, value)
        def result = new ByteArrayOutputStream()
        properties.store(result, 'Updated by Rhiot')
        result.close()

        sshClient.scp(new ByteArrayInputStream(result.toByteArray()), new File(file))

        ["Updated ${file} property ${property} to value ${value}"]
    }

}