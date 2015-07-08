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

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.camellabs.iot.deployer.Deployer

import java.text.SimpleDateFormat

import static com.camellabs.iot.performance.HardwareKit.RPI2
import static java.util.concurrent.TimeUnit.MINUTES

class PerformanceTester {

    private final def testResolver = new TestResolver()

    private final def deployer = new Deployer()

    private final mqttServer = new MqttServer().start()

    public static void main(String[] args) {
        def tester = new PerformanceTester()
        tester.runTestsForKit(args.size() == 0 ? RPI2 : args[0])
        tester.mqttServer.stop()
    }

    def runTestsForKit(String kit) {
        testResolver.testsForKit(kit).each { test ->
            def device = deployer.deploy(test.additionalProperties())

            MINUTES.sleep(3)
            def json = new ObjectMapper()
            def startedString = json.readValue(
                    new URL("http://${device.address().hostAddress}:8080/jolokia/read/org.apache.camel:context=camel-1,type=routes,name=\"mockSensorConsumer\"/ResetTimestamp"),
                    Map.class)['value'].toString().replaceAll(/\+\d\d:\d\d/, '')
            def started = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(startedString)
            def finished = new Date()
            def processed = json.readValue(
                    new URL("http://${device.address().hostAddress}:8080/jolokia/read/org.apache.camel:context=camel-1,type=routes,name=\"mockSensorConsumer\"/ExchangesTotal"),
                    Map.class)['value'].toString().toLong()
            def processingTime = finished.time - started.time
            println("Processed $processed messages in $processingTime.")
            println("Processed ${processed / (processingTime / 1000)} messages per second.")
        }
    }

}
