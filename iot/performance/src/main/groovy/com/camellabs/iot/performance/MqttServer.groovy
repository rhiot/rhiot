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
package com.camellabs.iot.performance

import org.apache.activemq.broker.BrokerService

import static org.springframework.util.SocketUtils.findAvailableTcpPort

class MqttServer {

    static final int mqttPort = findAvailableTcpPort();

    def broker = new BrokerService();

    BrokerService start() {
        broker.setPersistent(false);
        broker.addConnector("mqtt://0.0.0.0:" + mqttPort);
        broker.start()
        broker
    }

    def stop() {
        broker.stop()
    }

}
