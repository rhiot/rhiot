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
package io.rhiot.cloudplatform.service.binding;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.NoTypeConversionAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Camels {

    private static final Logger LOG = LoggerFactory.getLogger(Camels.class);

    // Collaborators

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Operations

    public List<?> convert(CamelContext camelContext, List<?> values, Class[] targetTypes) {
        LOG.debug("About to convert those arguments: {}", values);
        List<Object> convertedArguments = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            try {
                convertedArguments.add(camelContext.getTypeConverter().mandatoryConvertTo(targetTypes[i], values.get(i)));
            } catch (NoTypeConversionAvailableException e) {
                LOG.debug("Failed to convert {} to type {}. Falling back to Jackson conversion.",
                        values.get(i), targetTypes[i]);
                convertedArguments.add(objectMapper.convertValue(values.get(i), targetTypes[i]));
            }
        }
        LOG.debug("Converted arguments: {}", convertedArguments);
        return convertedArguments;
    }

}