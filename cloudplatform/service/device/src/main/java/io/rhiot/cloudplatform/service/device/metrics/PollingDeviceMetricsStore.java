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
package io.rhiot.cloudplatform.service.device.metrics;

import io.rhiot.cloudplatform.connector.IoTConnector;
import io.rhiot.cloudplatform.service.device.api.Device;
import io.rhiot.cloudplatform.service.device.api.DeviceRegistry;

import static io.rhiot.cloudplatform.connector.Header.arguments;

public abstract class PollingDeviceMetricsStore implements DeviceMetricsStore {

    private final IoTConnector connector;

    private final DeviceRegistry deviceRegistry;

    public PollingDeviceMetricsStore(IoTConnector connector, DeviceRegistry deviceRegistry) {
        this.connector = connector;
        this.deviceRegistry = deviceRegistry;
    }

    @Override
    public Object read(String deviceId, String metric) {
        Device device = deviceRegistry.get(deviceId);
        if (device == null) {
            return null;
        }

        if (device.getAddress() != null) {
            Object metricValue = connector.fromBus("deviceMetricsPoll.read", Object.class, arguments(deviceId, metric));
            if (metricValue != null) {
                write(deviceId, metric, metricValue);
                return metricValue;
            }
        }
        return doRead(deviceId, metric);
    }

    public abstract Object doRead(String deviceId, String metric);

}
