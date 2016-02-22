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
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class BrewInstallerTest {
    private static DefaultInstaller installer = new BrewInstaller();
    
    @BeforeClass
    public static void assumptions(){
        assumeTrue(IS_OS_MAC);
        assumeTrue(installer.isCommandInstalled("brew"));
    }
    
    @Test
    public void testNoSuchKeg() throws Exception {
        assertFalse(installer.isInstalled(Uuids.uuid()));
    }
    
    @Test
    public void testInstall() throws Exception {
        assumeTrue(!installer.isInstalled("clockywock"));
        installer.install("clockywock");
        assertTrue(installer.isInstalled("clockywock"));
        installer.uninstall("clockywock");
    }
    
    @Test
    public void testInstallList() throws Exception {
        installer.uninstall("clockywock,cowsay");
        assumeTrue(!installer.isInstalled("clockywock"));
        assumeTrue(!installer.isInstalled("cowsay"));
        
        String packages = "clockywock,cowsay";
        
        installer.install(packages);
        assertTrue(installer.isInstalled(Arrays.asList("cowsay", "clockywock")));
        installer.uninstall(packages);
        assertFalse(installer.isInstalled(Arrays.asList("cowsay", "clockywock")));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIncorrectPlatform() throws Exception {
        if (!IS_OS_LINUX) {
            new DefaultInstaller().install("foo");
        }
    }
}