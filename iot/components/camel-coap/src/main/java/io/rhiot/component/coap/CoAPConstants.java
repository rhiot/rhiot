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
package io.rhiot.component.coap;

public final class CoAPConstants {
    public static final String CAMEL_COAP_URL_PATTERN = "(?<host>[^:]+)(:(?<port>[0-9]+))";
    public static final String COAP_URI_ATTRIBUT = "coapUri";
    public static final String COAP_TIMEOUT_ATTRIBUT = "coapTimeout";

    public static final String COAP_URI = "CamelCoAPUri";
    public static final String COAP_METHOD = "CamelCoAPMethod";
    public static final String COAP_MEDIA_TYPE = "CamelCoAPMediaType";
    public static final String COAP_RESPONSE_CODE = "CamelCoAPResponseCode";
    public static final String COAP_RESPONSE_OPTIONS = "CamelCoAPResponseOptions";
    public static final String COAP_TIMEOUT = "CamelCoAPTimeout";
}
