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
package io.rhiot.component.deviceio.i2c.util;

import io.rhiot.component.deviceio.i2c.driver.I2CDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple driver tester
 */
public class DriverMain {

    private final static Logger LOG = LoggerFactory.getLogger(DriverMain.class);

    public static void main(String[] args) throws Exception {

        if (args != null && args.length == 1) {
            Class clazz = DriverMain.class.getClassLoader().loadClass(args[0]);

            I2CDriver driver = (I2CDriver) clazz.newInstance();

            driver.start();

            LOG.info(driver.get().toString());

            driver.stop();

            driver.shutdown();

        } else {
            LOG.error("Please provide 1 arg (fqn.driverClass)");
        }
    }
}
