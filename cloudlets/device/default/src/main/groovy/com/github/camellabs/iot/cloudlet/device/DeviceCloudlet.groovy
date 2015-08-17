/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.cloudlet.device

import com.github.camellabs.iot.cloudlet.device.verticles.LeshanServerVeritcle
import com.github.camellabs.iot.cloudlet.device.verticles.RestApiVerticle
import io.vertx.groovy.core.Vertx

class DeviceCloudlet {

    private final def vertx = Vertx.vertx()

    DeviceCloudlet start() {
        vertx.deployVerticle("groovy:${LeshanServerVeritcle.class.name}")
        vertx.deployVerticle("groovy:${RestApiVerticle.class.name}")
        return this
    }

    public static void main(String[] args) {
        new DeviceCloudlet().start()
    }

}
