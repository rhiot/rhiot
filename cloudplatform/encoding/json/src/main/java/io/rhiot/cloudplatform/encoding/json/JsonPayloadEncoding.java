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
package io.rhiot.cloudplatform.encoding.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class JsonPayloadEncoding implements PayloadEncoding {

    private final ObjectMapper objectMapper;

    public JsonPayloadEncoding(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonPayloadEncoding() {
        this(new ObjectMapper().setSerializationInclusion(NON_NULL));
    }

    @Override
    public byte[] encode(Object payload) {
        try {
            Map<String, Object> wrappedPayload = new HashMap<>();
            wrappedPayload.put("payload", payload);
            return objectMapper.writeValueAsBytes(wrappedPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object decode(byte[] payload) {
        try {
            return objectMapper.readValue(payload, Map.class).get("payload");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}