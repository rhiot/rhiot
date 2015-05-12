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
package com.github.camellabs.component.pubnub.example;

import java.util.Random;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.json.JSONException;
import org.json.JSONObject;

public class PubNubSensor2Example {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new PubsubRoute());
        main.addRouteBuilder(new SimulatedDeviceEventGeneratorRoute());
        main.run();
    }

    static private class SimulatedDeviceEventGeneratorRoute extends RouteBuilder {
        private String deviceEP = "pubnub:pubsub:iot?uuid=device2&publisherKey=" + PubNubExampleConstants.PUBNUB_PUBLISHER_KEY + "&subscriberKey="
                                  + PubNubExampleConstants.PUBNUB_SUBSCRIBER_KEY;

        @Override
        public void configure() throws Exception {
            //@formatter:off
            from("timer:device2?delay=1000").routeId("device-event-route")
            .bean(PubNubSensor2Example.EventGeneratorBean.class, "getRandomEvent('device2')")
            .convertBodyTo(JSONObject.class)
            .to(deviceEP);
            
            //TODO figure out best way to tell master channel the device private channel, so device/master can use unicast.
            // special property or part of state json object.
            from(deviceEP)
            .routeId("device-private-channel-route")
            .log("private message from master to device2 : ${body}");
            //@formatter:on
        }
    }

    static private class PubsubRoute extends RouteBuilder {
        private String masterEP = "pubnub:pubsub:iot?uuid=master&subscriberKey=" + PubNubExampleConstants.PUBNUB_SUBSCRIBER_KEY;

        @Override
        public void configure() throws Exception {
            //@formatter:off
            from(masterEP)
            .routeId("master-route")
            .log("${body} headers : ${headers}").to("mock:result");
            //@formatter:on
        }
    }

    public static class EventGeneratorBean {
        public static String getRandomEvent(String device) throws JSONException {
            Random rand = new Random();
            String s = "{device:" + device + ", humidity:" + rand.nextInt(100) + ", temperatur=" + rand.nextInt(40) + "}";
            return s;
        }
    }
}
