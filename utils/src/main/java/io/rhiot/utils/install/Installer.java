package io.rhiot.utils.install;

import java.util.List;

/**
 * Install any packages required by the components, eg gpsd for camel-gpsd
 */
public interface Installer {
    
    /**
     * Ensures all packages are installed
     * @param packages packages to verify
     * @return true if all packages are installed, false otherwise.
     */
    boolean isInstalled(List<String> packages);

    /**
     * Returns true if the package is installed, false otherwise.
     *
     * @param packageName package name, eg gpsd
     * @return returns true if the package is installed, false otherwise.
     *
     */
    boolean isInstalled(String packageName);
    /**
     * Installs 1 or more packages, comma or space separated.
     *
     * @param packageNames package name, eg gpsd
     *
     */
    boolean install(String packageNames);
    /**
     * Uninstalls packages, space or comma separated.
     *
     * @param packageNames package names, eg gpsd
     *
     */
    void uninstall(String packageNames);
}
