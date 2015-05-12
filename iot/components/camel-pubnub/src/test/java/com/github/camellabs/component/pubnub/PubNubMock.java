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
package com.github.camellabs.component.pubnub;

import java.util.HashMap;
import java.util.Map;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import org.json.JSONObject;

public class PubNubMock extends Pubnub {
    private Map<String, Callback> subscribers = new HashMap<String, Callback>();

    public PubNubMock(String publish_key, String subscribe_key) {
        super(publish_key, subscribe_key);
    }

    @Override
    public void subscribe(String channel, Callback callback) throws PubnubException {
        subscribers.put(channel, callback);
        callback.connectCallback(channel, "OK");
    }

    @Override
    public void publish(String channel, JSONObject message, Callback callback) {
        callback.successCallback(channel, "OK");
        Callback clientMockCallback = subscribers.get(channel);
        if (clientMockCallback != null) {
            clientMockCallback.successCallback(channel, message, "111111111111");
        }
    }
}
