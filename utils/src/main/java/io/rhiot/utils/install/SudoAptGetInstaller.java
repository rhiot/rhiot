package io.rhiot.utils.install;

public class SudoAptGetInstaller extends AptGetInstaller {

    public SudoAptGetInstaller() {
        super("sudo apt-get install", "sudo apt-get remove", "dpkg -s", "Status: install ok installed");
    }
}
