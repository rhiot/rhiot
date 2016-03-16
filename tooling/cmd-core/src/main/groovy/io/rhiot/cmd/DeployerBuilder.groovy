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
package io.rhiot.cmd

import io.rhiot.scanner.DeviceDetector
import io.rhiot.scanner.SimplePortScanningDeviceDetector

class DeployerBuilder {

    private DeviceDetector deviceDetector = new SimplePortScanningDeviceDetector()

    private String username = 'pi'

    private String password = 'raspberry'

    private boolean debug

    Cmd build() {
        new Cmd(deviceDetector, username, password, debug)
    }

    DeployerBuilder deviceDetector(DeviceDetector deviceDetector) {
        this.deviceDetector = deviceDetector
        this
    }

    DeployerBuilder username(String username) {
        this.username = username
        this
    }

    DeployerBuilder password(String password) {
        this.password = password
        this
    }

    DeployerBuilder debug(boolean debug) {
        this.debug = debug
        this
    }

}