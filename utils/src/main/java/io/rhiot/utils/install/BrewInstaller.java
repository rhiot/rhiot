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

import java.util.List;

public class BrewInstaller extends DefaultInstaller {

    private static final String INSTALL_COMMAND = "brew install";
    private static final String UNINSTALL_COMMAND = "brew remove";
    private static final String IS_INSTALLED_COMMAND = "brew list";

    public BrewInstaller() {
        super(INSTALL_COMMAND, UNINSTALL_COMMAND, IS_INSTALLED_COMMAND);
    }

    @Override
    public boolean isPlatformSupported() {
        return OsUtils.isLinux() || OsUtils.isMac();
    }

    @Override
    public boolean confirmInstalled(String packageName, List<String> output){
        return output.size() > 0 && !output.get(0).contains("No such keg");
    }

}
