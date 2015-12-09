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
     * @return Returns true if all packages were/are installed.
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
