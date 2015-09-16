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
package io.rhiot.vertx

import io.vertx.groovy.core.eventbus.Message

import static java.util.Optional.empty
import static org.apache.commons.lang3.StringUtils.isBlank

final class Vertxes {

    private Vertxes() {
    }

    static Optional<String> assertStringBody(Message message, String information) {
        if(message.body() == null || isBlank(message.body().toString())) {
            message.fail(-1, information)
            return empty()
        }
        Optional.of(message.body().toString())
    }

    static Optional<String> assertStringBody(Message message) {
        assertStringBody(message, 'Expected text body. Found blank.')
    }


}
