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
package io.rhiot.component.bluetooth;

import io.rhiot.utils.install.DefaultInstaller;
import io.rhiot.utils.install.Installer;

import java.util.Map;
import java.util.Set;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothComponent extends UriEndpointComponent {

    private static final Logger LOG = LoggerFactory.getLogger(BluetoothComponent.class);

    private BluetoothDevicesProvider bluetoothDevicesProvider;

    private Installer installer;

    private boolean ignoreInstallerProblems = true;

    private String requiredPackages = BluetoothConstants.BLUETOOTH_DEPENDENCIES_LINUX;

    public BluetoothComponent() {
        super(BluetoothEndpoint.class);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        BluetoothEndpoint endpoint = new BluetoothEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }

    // Collaborators getters and setters

    public BluetoothDevicesProvider getBluetoothDevicesProvider() {
        return bluetoothDevicesProvider;
    }

    public void setBluetoothDevicesProvider(BluetoothDevicesProvider bluetoothDevicesProvider) {
        this.bluetoothDevicesProvider = bluetoothDevicesProvider;
    }

    public Installer getInstaller() {
        return installer;
    }

    public void setInstaller(Installer installer) {
        this.installer = installer;
    }

    public boolean isIgnoreInstallerProblems() {
        return ignoreInstallerProblems;
    }

    public void setIgnoreInstallerProblems(boolean ignoreInstallerProblems) {
        this.ignoreInstallerProblems = ignoreInstallerProblems;
    }

    public String getRequiredPackages() {
        return requiredPackages;
    }

    public void setRequiredPackages(String requiredPackages) {
        this.requiredPackages = requiredPackages;
    }

    // Helpers

    protected void installDependencies() {
        installer = resolveInstaller();

        String requiredPackages = getRequiredPackages();

        try {
            if (!installer.install(requiredPackages) && !ignoreInstallerProblems) {
                throw new IllegalStateException("Failed to install dependencies");
            }
        } catch (Exception ex) {
            if (ignoreInstallerProblems) {
                LOG.warn(ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

    protected Installer resolveInstaller() {
        LOG.debug("Started resolving Installer...");
        if (installer != null) {
            LOG.debug("Installer has been set on the component level. Camel will use it: {}", installer);
            return installer;
        }
        Set<Installer> installers = getCamelContext().getRegistry().findByType(Installer.class);
        if (installers.isEmpty()) {
            LOG.debug("No Installer found in the registry - creating new DefaultInstaller.");
            return new DefaultInstaller();
        } else if (installers.size() == 1) {
            return installers.iterator().next();
        } else {
            return new DefaultInstaller();
        }
    }
}
