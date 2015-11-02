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

package io.rhiot.utils.install;

import io.rhiot.utils.OsUtils;
import io.rhiot.utils.install.exception.PermissionDeniedException;
import io.rhiot.utils.process.CommonsExecProcessManager;
import io.rhiot.utils.process.ProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Generic installer for Rhiot components, eg camel-gpsd requires GPSD and camel-camera requires Video for Linux.
 */
public abstract class Installer {

    private String installCommand;
    private String uninstallCommand;
    private String isInstalledCommand;

    //Success / failure results
    private String installSuccess;

    public Installer() {
    }

    public Installer(String installCommand, String uninstallCommand, String isInstalledCommand) {
        this.installCommand = installCommand;
        this.uninstallCommand = uninstallCommand;
        this.isInstalledCommand = isInstalledCommand;
    }

    public Installer(String installCommand, String uninstallCommand, String isInstalledCommand, String installSuccess) {
        this.installCommand = installCommand;
        this.uninstallCommand = uninstallCommand;
        this.isInstalledCommand = isInstalledCommand;
        this.installSuccess = installSuccess;
    }

    private Logger LOG = LoggerFactory.getLogger(Installer.class);
    
    private ProcessManager processManager = new CommonsExecProcessManager();

    /**
     * True if the given command is installed in the shell, false otherwise.
     * @param command eg brew, aptitude, sudo etc
     * @return <tt>true</tt> if the given command is installed in the shell, false otherwise.
     */
    public boolean isCommandInstalled(String command){
        List<String> output = getProcessManager().executeAndJoinOutput(command);
        return output.size() > 0 && !output.get(0).contains("command not found");
    }

    /**
     * Ensures all packages are installed
     * @param packages packages to verify
     * @return true if all packages are installed, false otherwise.
     */
    public boolean isInstalled(List<String> packages){
        if (packages == null || packages.size() == 0) {
            return true;
        } else {
            return !packages.stream().filter(s -> !isInstalled(s)).findFirst().isPresent();
        }
    }

    /**
     * Returns true if the package is installed, false otherwise.
     *
     * @param packageName package name, eg gpsd
     * @return returns true if the package is installed, false otherwise.
     *                    
     */
    public boolean isInstalled(String packageName) {
        if (!isPlatformSupported()) {
            throw new UnsupportedOperationException();
        }
        
        boolean installed = confirmInstalled(packageName, getProcessManager().executeAndJoinOutput((getIsInstalledCommand() + " " + packageName).split(" ")));
        LOG.info("[{}] isInstalled ? {} ", packageName, installed);
        return installed;
    }


    /**
     * Checks the output to confirm the package is installed.
     *
     * @param packageName package name, eg gpsd
     * @param output result of the installation process  
     * @return returns true if the package is installed, false otherwise.
     *
     */
    public boolean confirmInstalled(String packageName, List<String> output){
        if (output != null) {
            return output.stream().filter(s -> s != null && s.contains(getInstallSuccess())).findAny().isPresent();
        } else {
            return true; //may be successfull;
        }
    }

    /**
     * Installs 1 or more packages, comma or space separated.
     *
     * @param packageNames package name, eg gpsd
     *
     */
    public boolean install(String packageNames) {
        if (!isPlatformSupported()) {
            throw new UnsupportedOperationException();
        } 
        
        
        LOG.info("Installing [{}]", packageNames);
        String normalizedNames = packageNames.replaceAll(",", " ");
        
        if (isInstalled(Arrays.asList(normalizedNames.split(" ")))) {
            LOG.info("Already installed");
            return true;
        }

        List<String> output = getProcessManager().executeAndJoinOutput((String.join(" ", (getInstallCommand() + " " + normalizedNames).split(" "))));
        
        LOG.info("Installation result : {}", output);
        if (isPermissionDenied(output)) {
            throw new PermissionDeniedException("You must have sufficient privileges to install package/s [" + packageNames + "]");
        }
        
        return isInstalled(Arrays.asList(normalizedNames.split(" ")));
    }

    /**
     * Uninstalls packages, space or comma separated.
     *
     * @param packageNames package names, eg gpsd
     *
     */
    public void uninstall(String packageNames) {
        if (!isPlatformSupported()) {
            throw new UnsupportedOperationException();
        }
        
        LOG.info("Uninstalling [{}]", packageNames);

        List<String> output = getProcessManager().executeAndJoinOutput((getUninstallCommand() + " " + packageNames.replaceAll(",", " ")).split(" "));
        
        LOG.info("Uninstallation result : {}", output);
        if (isPermissionDenied(output)) {
            throw new PermissionDeniedException("You must have sufficient privileges to uninstall package [" + packageNames + "]");
        }
        
    }

    protected boolean isPermissionDenied(List<String> output){
        return output.stream().filter(s -> s.contains("Permission denied")).findAny().isPresent();
    }

    public String getInstallCommand() {
        return installCommand;
    }

    public void setInstallCommand(String installCommand) {
        this.installCommand = installCommand;
    }

    public String getUninstallCommand() {
        return uninstallCommand;
    }

    public void setUninstallCommand(String uninstallCommand) {
        this.uninstallCommand = uninstallCommand;
    }

    public String getIsInstalledCommand() {
        return isInstalledCommand;
    }

    public void setIsInstalledCommand(String isInstalledCommand) {
        this.isInstalledCommand = isInstalledCommand;
    }

    public String getInstallSuccess() {
        return installSuccess;
    }

    public void setInstallSuccess(String installSuccess) {
        this.installSuccess = installSuccess;
    }

    /**
     * Returns true if the installer can run on the current platform, false otherwise.
     * @return true if the installer can run on the current platform, false otherwise.
     */
    public boolean isPlatformSupported(){
        return OsUtils.isPlatform("linux");
    }


    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }
}
