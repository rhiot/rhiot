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
package io.rhiot.component.kura.router;

import java.util.Dictionary;
import java.util.Map;

import org.apache.camel.component.properties.PropertiesFunction;
import org.osgi.service.cm.Configuration;

public class RhiotKuraMetatypePropertiesFunction implements PropertiesFunction {
    Map<String, Object> m_properties;
    Dictionary<String, Object> m_dictonnary;

    public RhiotKuraMetatypePropertiesFunction(Map<String, Object> properties) {
        m_properties = properties;
    }

    public RhiotKuraMetatypePropertiesFunction(Configuration config) {
        m_dictonnary = config.getProperties();
    }

    @Override
    public String getName() {
        return RhiotKuraConstants.METATYPE_NAME;
    }

    @Override
    public String apply(String remainder) {
        if (m_properties != null) {
            return (m_properties.get(remainder) != null) ? m_properties.get(remainder).toString() : null;
        } else if (m_dictonnary != null) {
            return (m_dictonnary.get(remainder) != null) ? m_dictonnary.get(remainder).toString() : null;
        }
        return null;
    }
}
