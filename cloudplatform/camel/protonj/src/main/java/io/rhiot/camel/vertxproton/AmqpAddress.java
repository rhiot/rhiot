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
package io.rhiot.camel.vertxproton;

public class AmqpAddress {

    private final String host;

    private final int port;

    private final String path;

    public AmqpAddress(String uri) {
        String normalizedUri = uri.replaceFirst("amqp:", "").replaceFirst("//", "");
        int indexOfPathSeparator = normalizedUri.indexOf('/');
        this.path = indexOfPathSeparator > 0 ? normalizedUri.substring(indexOfPathSeparator + 1) : "";

        String normalizedCoordinates = normalizedUri.replaceAll("/.*", "");
        String[] coordinates = normalizedCoordinates.split(":");
        this.host = coordinates[0];
        this.port = Integer.parseInt(coordinates[1]);
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String path() {
        return path;
    }

    public boolean isServer() {
        return host.startsWith("~");
    }

}
