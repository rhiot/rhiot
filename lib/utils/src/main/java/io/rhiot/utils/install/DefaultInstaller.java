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

import io.rhiot.utils.install.exception.PermissionDeniedException;
import io.rhiot.utils.process.ExecProcessManager;
import io.rhiot.utils.process.ProcessManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Default installer for Rhiot components using apt-get, eg camel-gpsd requires GPSD and camel-webcam may require Video for Linux for some distros.
 */
public class DefaultInstaller implements Installer {

    public static final String DEFAULT_INSTALL_COMMAND = "apt-get -q -y install";
    public static final String DEFAULT_UNINSTALL_COMMAND = "apt-get -q -y remove";
    public static final String DEFAULT_IS_INSTALLED_COMMAND = "dpkg -s";
    public static final String DEFAULT_INSTALL_SUCCESS = "Status: install ok installed";
    public static final String DEFAULT_COMMAND_NOT_FOUND_MESSAGE = "command not found";
    public static final String PERMISSION_DENIED_MESSAGE = "Permission denied";

    private static final Logger LOG = getLogger(DefaultInstaller.class);


    public static final int DEFAULT_TIMEOUT = 60000 * 10; //10 minutes max to install

    private int timeout = DEFAULT_TIMEOUT;

    private String installCommand = DEFAULT_INSTALL_COMMAND;
    private String uninstallCommand = DEFAULT_UNINSTALL_COMMAND;
    private String isInstalledCommand = DEFAULT_IS_INSTALLED_COMMAND;

    //Success / failure results
    private String installSuccess = DEFAULT_INSTALL_SUCCESS;

    public DefaultInstaller() {
    }

    public DefaultInstaller(String installCommand, String uninstallCommand, String isInstalledCommand) {
        this.installCommand = installCommand;
        this.uninstallCommand = uninstallCommand;
        this.isInstalledCommand = isInstalledCommand;
    }

    public DefaultInstaller(String installCommand, String uninstallCommand, String isInstalledCommand, String installSuccess) {
        this.installCommand = installCommand;
        this.uninstallCommand = uninstallCommand;
        this.isInstalledCommand = isInstalledCommand;
        this.installSuccess = installSuccess;
    }
    
    private ProcessManager processManager = new ExecProcessManager(getTimeout());

    /**
     * True if the given command is installed in the shell, false otherwise.
     * @param command eg brew, aptitude, sudo etc
     * @param failureMessage the message expected upon failure. 
     * @return <tt>true</tt> if the given command is installed in the shell, false otherwise.
     */
    public boolean isCommandInstalled(String command, String failureMessage){
        List<String> output = getProcessManager().executeAndJoinOutput(command);
        return output.size() > 0 && !output.get(0).contains(failureMessage);
    }

    /**
     * True if the given command is installed in the shell, false otherwise.
     * @param command eg brew, aptitude, sudo etc
     * @return <tt>true</tt> if the given command is installed in the shell, false otherwise.
     */
    public boolean isCommandInstalled(String command){
        return isCommandInstalled(command, DEFAULT_COMMAND_NOT_FOUND_MESSAGE);
    }

    @Override
    public boolean isInstalled(List<String> packages){
        if (packages == null || packages.size() == 0) {
            return true;
        } else {
            return !packages.stream().anyMatch(s -> !isInstalled(s));
        }
    }

    @Override
    public boolean isInstalled(String packageName) {
        if (!isPlatformSupported()) {
            throw new UnsupportedOperationException("DefaultInstaller does not support this platform");
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
            return output.stream().anyMatch(s -> s != null && s.contains(getInstallSuccess()));
        } else {
            return true; //may be successful
        }
    }

    @Override
    public boolean install(String packageNames) {
        if (!isPlatformSupported()) {
            throw new UnsupportedOperationException("DefaultInstaller does not support this platform");
        } 
        
        if (StringUtils.isEmpty(packageNames)){
            return true;
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

    @Override
    public void uninstall(String packageNames) {
        if (!isPlatformSupported()) {
            throw new UnsupportedOperationException("DefaultInstaller does not support this platform");
        }
        
        LOG.info("Uninstalling [{}]", packageNames);

        List<String> output = getProcessManager().executeAndJoinOutput((getUninstallCommand() + " " + packageNames.replaceAll(",", " ")).split(" "));
        
        LOG.info("Uninstall result : {}", output);
        if (isPermissionDenied(output)) {
            throw new PermissionDeniedException("You must have sufficient privileges to uninstall package/s [" + packageNames + "]");
        }
        
    }

    /**
     * Returns true if permission was denied.
     * @param output Output from the install/uninstall process.
     * @return true if permission was denied.
     */
    protected boolean isPermissionDenied(List<String> output){
        return output.stream().anyMatch(s -> s != null && s.contains(PERMISSION_DENIED_MESSAGE));
    }


    /**
     * Returns true if the installer can run on the current platform, false otherwise. Default is Linux.
     * @return true if the installer can run on the current platform, false otherwise.
     */
    public boolean isPlatformSupported(){
        return IS_OS_LINUX;
    }
    
    // Accessors

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


    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
