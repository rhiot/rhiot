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
package io.rhiot.cloudlets.device

import io.rhiot.cloudlets.device.verticles.LeshanServerVerticle
import io.rhiot.cloudlets.device.verticles.DeviceRestApiVerticle
import io.vertx.groovy.core.Vertx

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS

/**
 * Bootstrap class for the Device Cloudlet microservice.
 */
class DeviceCloudlet {

    // Members

    private static final def isStarted = new CountDownLatch(2)

    // Collaborators

    private final def vertx = Vertx.vertx()

    // Lifecycle operations

    DeviceCloudlet start() {
        vertx.deployVerticle("groovy:${LeshanServerVerticle.class.name}")
        vertx.deployVerticle("groovy:${DeviceRestApiVerticle.class.name}")
        return this
    }

    static void main(String... args) {
        new DeviceCloudlet().start()
    }

    DeviceCloudlet waitFor() {
        isStarted.await(30, SECONDS)
        this
    }

}
