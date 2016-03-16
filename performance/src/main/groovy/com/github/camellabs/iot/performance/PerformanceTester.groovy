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
package com.github.camellabs.iot.performance

import com.fasterxml.jackson.databind.ObjectMapper
import io.rhiot.cmd.ConsoleInformation
import io.rhiot.cmd.Cmd
import io.rhiot.cmd.DeployerBuilder

import static HardwareKit.RPI2
import static java.util.concurrent.TimeUnit.MINUTES

class PerformanceTester {

    // Collaborators

    private final TestResolver testResolver

    private final Cmd deployer

    private final MqttServer mqttServer

    // Listeners

    private final List<ResultsProcessor> resultsProcessors

    // Constructors

    PerformanceTester(TestResolver testResolver, Cmd deployer, mqttServer, List<ResultsProcessor> resultsProcessors) {
        this.testResolver = testResolver
        this.deployer = deployer
        this.mqttServer = mqttServer
        this.resultsProcessors = resultsProcessors
    }

    PerformanceTester() {
        this(new DefaultTestResolver(), new DeployerBuilder().build(), new MqttServer().start(), [new StdoutResultsProcessor(), new ChartResultsProcessor()])
    }

    // Running tests

    def runTestsForKit(String kit) {
        def tests = testResolver.testsForKit(kit)
        if(tests.isEmpty()) {
            throw new ConsoleInformation("No tests found for hardware kit ${kit}.")
        }
        tests.collect { test ->
            def device = deployer.deploy(test.additionalProperties())

            MINUTES.sleep(3)
            def json = new ObjectMapper()
            def processingTime = test.processingTime(device)
            def processed = json.readValue(
                    new URL("http://${device.address().hostAddress}:8778/jolokia/read/org.apache.camel:context=SingletonVertxCamelContext,type=routes,name=%22mockSensorConsumer%22/ExchangesTotal"),
                    Map.class)['value'].toString().toLong()
            def result = new TestResult(test.testGroup(), test.variationLabel(), processed, processingTime)
            resultsProcessors.each { it.processResult(result) }
        }
        resultsProcessors.each { it.complete() }
    }

    // Main handler

    public static void main(String... args) {
        def tester = new PerformanceTester()
        try {
            tester.runTestsForKit(args.size() == 0 ? RPI2 : args[0])
        } catch(ConsoleInformation info) {
            println(info.message)
        } finally {
            tester.mqttServer.stop()
            tester.deployer.close()
        }
    }

}
