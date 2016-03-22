/**
 * Licensed to the Eclipse Foundation under one or more
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
package io.rhiot.cloudplatform.service.device.api;

import static java.lang.String.format;

/**
 * Provides device-related constants, in particular AMQP channel helpers.
 */
public final class DeviceConstants {

    // Constants

    public static final String CHANNEL_DEVICE_GET = "device.get";

    public static final String CHANNEL_DEVICE_GET_BY_REGISTRATION_ID = "device.getByRegistrationId";

    public static final String CHANNEL_DEVICE_LIST = "device.list";

    public static final String CHANNEL_DEVICE_DISCONNECTED = "device.disconnected";

    public static final String CHANNEL_DEVICE_REGISTER = "device.register";

    public static final String CHANNEL_DEVICE_UPDATE = "device.update";

    public static final String CHANNEL_DEVICE_DEREGISTER = "device.deregister";

    public static final String CHANNEL_DEVICE_HEARTBEAT = "device.heartbeat";

    // Device metrics constants

    public static final String CHANNEL_DEVICE_METRICS_READ = "device-metrics.read";

    public static final String CHANNEL_DEVICE_METRICS_READ_ALL = "device-metrics.readAll";

    public static final String CHANNEL_DEVICE_METRICS_WRITE = "device-metrics.write";

    // Constructors

    private DeviceConstants() {
    }

    // Channel helpers

    public static String getDevice(String deviceId) {
        return format("%s.%s", CHANNEL_DEVICE_GET, deviceId);
    }

    public static String getDeviceByRegistrationId(String registrationId) {
        return format("%s.%s", CHANNEL_DEVICE_GET_BY_REGISTRATION_ID, registrationId);
    }

    public static String listDevices() {
        return CHANNEL_DEVICE_LIST;
    }

    public static String disconnected() {
        return CHANNEL_DEVICE_DISCONNECTED;
    }

    public static String registerDevice() {
        return CHANNEL_DEVICE_REGISTER;
    }

    public static String updateDevice() {
        return CHANNEL_DEVICE_UPDATE;
    }

    public static String deregisterDevice(String deviceId) {
        return format("%s.%s", CHANNEL_DEVICE_DEREGISTER, deviceId);
    }

    public static String deviceHeartbeat(String deviceId) {
        return format("%s.%s", CHANNEL_DEVICE_HEARTBEAT, deviceId);
    }

    // Device metrics channel helpers

    public static String readDeviceMetric(String deviceId, String metric) {
        return format("%s.%s.%s", CHANNEL_DEVICE_METRICS_READ, deviceId, metric);
    }

    public static String readAllDeviceMetrics(String deviceId) {
        return format("%s.%s", CHANNEL_DEVICE_METRICS_READ_ALL, deviceId);
    }

    public static String writeDeviceMetric(String deviceId, String metric) {
        return format("%s.%s.%s", CHANNEL_DEVICE_METRICS_WRITE, deviceId, metric);
    }

}
