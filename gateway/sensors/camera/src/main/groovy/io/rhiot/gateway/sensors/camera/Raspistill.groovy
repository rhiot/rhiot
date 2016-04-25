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
package io.rhiot.gateway.sensors.camera

import io.rhiot.utils.process.ProcessManager

class Raspistill {

    private final ProcessManager processManager

    private final File workDirectory

    Raspistill(ProcessManager processManager, File workDirectory) {
        this.processManager = processManager
        this.workDirectory = workDirectory

        workDirectory.mkdir()
    }

    void timelapse() {
        processManager.asyncExecuteAndJoinOutput('raspistill', '-o', "${workDirectory.absolutePath}/camera.jpg", '-t', '21600000', '-tl', '200')
    }

}