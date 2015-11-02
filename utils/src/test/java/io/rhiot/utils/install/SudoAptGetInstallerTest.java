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
import io.rhiot.utils.Uuids;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.junit.Assume.assumeTrue;

public class SudoAptGetInstallerTest {
    
    private static Installer installer = new SudoAptGetInstaller();

    @BeforeClass
    public static void assumptions(){
        assumeTrue(OsUtils.isPlatform("linux"));
        assumeTrue(installer.isCommandInstalled("apt-get"));
    }

    @Test
    public void testNoSuchKeg() throws Exception {
        assertFalse(installer.isInstalled(Uuids.uuid()));
    }

    @Test
    public void testInstall() throws Exception {
        assumeTrue(!installer.isInstalled("clockywock"));
        installer.install("clockywock");
        installer.uninstall("clockywock");
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

    @Test(expected = UnsupportedOperationException.class)
    public void testIncorrectPlatform() throws Exception {
        if (!OsUtils.isPlatform("linux")) {
            new AptGetInstaller().install("foo");
        }
    }
}