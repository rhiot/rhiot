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

import io.rhiot.cloudplatform.connector.IoTConnector
import org.apache.camel.builder.RouteBuilder

import static io.rhiot.cloudplatform.connector.Header.arguments

class CameraSensor extends RouteBuilder {

    private final IoTConnector connector

    private final Raspistill raspistill

    private final File workdir

    private final String deviceId

    private final boolean enqueue

    private final boolean sendEnqueuedToCloud

    CameraSensor(IoTConnector connector, Raspistill raspistill, File workdir, String deviceId,
                 boolean enqueue, boolean sendEnqueuedToCloud) {
        this.connector = connector
        this.raspistill = raspistill
        this.workdir = workdir
        this.deviceId = deviceId
        this.enqueue = enqueue
        this.sendEnqueuedToCloud = sendEnqueuedToCloud
    }

    @Override
    void configure() {
        workdir.mkdirs()
        def queue = new File(workdir, 'queue')
        queue.mkdirs()

        raspistill.timelapse()

        if(enqueue) {
            from("file:${workdir.absolutePath}/?delay=250&noop=true&idempotent=false&fileName=camera.jpg").
                    to("file:${queue.absolutePath}?fileName=\${random(1,100000)}.jpg")

            // Send enqueued image data to a cloud
            if (sendEnqueuedToCloud) {
                from("file:${queue.absolutePath}").process {
                    connector.toBus('camera.process', it.in.getBody(InputStream.class), arguments(deviceId, 'eu'))
                }
            }
        }
    }

}