/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.cloudplatform.service.binding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class DestinationBinding {

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\$\\{.+?\\}");

    private final PropertyResolver propertyResolver;

    public DestinationBinding(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    public String destination(String pattern) {
        Matcher matcher = PROPERTY_PATTERN.matcher(pattern);
        while (matcher.find()) {
            String rawPropertyKey = matcher.group();
            String propertyKey = rawPropertyKey.substring(2, rawPropertyKey.length() - 1);
            String propertyValue = propertyResolver.resolveProperty(propertyKey);
            pattern = pattern.replace(rawPropertyKey, propertyValue);
        }
        return format("amqp:%s.>", pattern);
    }

}
