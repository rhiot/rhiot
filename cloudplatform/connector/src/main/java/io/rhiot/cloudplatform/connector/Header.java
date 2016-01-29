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
package io.rhiot.cloudplatform.connector;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to make it easier to attach headers to the messages sent to the IoT Connector.
 */
public class Header {

    // Members

    private final String key;

    private final Object value;

    // Constructors

    public Header(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    // Factory methods

    public static Header header(String key, Object value) {
        return new Header(key, value);
    }

    public static Header[] arguments(Object... arguments) {
        Header[] headers = new Header[arguments.length];
        for(int i = 0; i < arguments.length; i++) {
            headers[i] = header("RHIOT_ARG" + i, arguments[i]);
        }
        return headers;
    }

    public static Map<String, Object> arguments(Header[] arguments) {
        Map<String, Object> collectedHeaders = new HashMap<>();
        for(Header header : arguments) {
            collectedHeaders.put(header.key(), header.value());
        }
        return collectedHeaders;
    }

    public String key() {
        return key;
    }

    public Object value() {
        return value;
    }

}
