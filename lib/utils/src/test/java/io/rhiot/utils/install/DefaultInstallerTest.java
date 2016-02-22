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

import io.rhiot.utils.Uuids;
import io.rhiot.utils.install.exception.PermissionDeniedException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.junit.Assert.*;

import static org.junit.Assume.assumeTrue;

public class DefaultInstallerTest {
    
    private static DefaultInstaller installer = new DefaultInstaller();

    @BeforeClass
    public static void assumptions(){
        assumeTrue(IS_OS_LINUX);
        assumeTrue(installer.isCommandInstalled("apt-get"));
        
        boolean hasPermissions = true;
        //Ensure permissions to run installation
        try {
            installer.install("clockywock");
            installer.uninstall("clockywock");
        } catch (PermissionDeniedException e) {
            hasPermissions = false;
        }
        assumeTrue(hasPermissions);
    }

    @Test
    public void testNoSuchPackage() throws Exception {
        assertFalse(installer.isInstalled(Uuids.uuid()));
    }

    @Test
    public void testInstallList() throws Exception {
        assumeTrue(!installer.isInstalled("clockywock"));
        assumeTrue(!installer.isInstalled("cowsay"));

        String packages = "clockywock,cowsay";

        installer.install(packages);
        assertTrue(installer.isInstalled("clockywock"));
        assertTrue(installer.isInstalled("cowsay"));
        installer.uninstall(packages);
    }
    
    @Test
    public void testConfirmInstalled(){
        ArrayList<String> output = new ArrayList<>();
        output.add(DefaultInstaller.DEFAULT_INSTALL_SUCCESS);
        assertTrue(installer.confirmInstalled("test", output));
    }
    
    @Test
    public void testConfirmInstalledFails(){
        ArrayList<String> output = new ArrayList<>();
        assertFalse(installer.confirmInstalled("test", output));
    }
    
    @Test
    public void testPermissionDenied(){
        ArrayList<String> output = new ArrayList<>();
        output.add(DefaultInstaller.PERMISSION_DENIED_MESSAGE);
        assertTrue(installer.isPermissionDenied(output));
    }
    
    @Test
    public void testPermissionNotDenied(){
        ArrayList<String> output = new ArrayList<>();
        output.add(null);
        assertFalse(installer.isPermissionDenied(output));
    }
}