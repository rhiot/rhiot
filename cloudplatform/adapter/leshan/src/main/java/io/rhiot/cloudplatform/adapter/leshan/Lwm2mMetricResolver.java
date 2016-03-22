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
package io.rhiot.cloudplatform.adapter.leshan;

import io.rhiot.cloudplatform.service.device.api.DeviceMetrics;

public class Lwm2mMetricResolver implements MetricResolver {

    @Override
    public String resolveMetric(String metric) {
        try {
            switch (DeviceMetrics.valueOf(metric)) {
                case firmwareVersion: {
                    return "/3/0/3";
                }
                default: return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}