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
package io.rhiot.cloudplatform.service.device;

import io.rhiot.datastream.schema.device.Device;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class InMemoryDeviceRegistry implements DeviceRegistry {

    private final Map<String, Device> devices = new ConcurrentHashMap<>();

    private final long disconnectionPeriod;

    public InMemoryDeviceRegistry(long disconnectionPeriod) {
        this.disconnectionPeriod = disconnectionPeriod;
    }

    @Override
    public Device get(String deviceId) {
        return devices.get(deviceId);
    }

    @Override
    public Device getByRegistrationId(String registrationId) {
        return devices.values().stream().filter(device -> registrationId.equals(device.getRegistrationId())).findFirst().orElse(null);
    }

    @Override
    public List<Device> list() {
        return new ArrayList<>(devices.values());
    }

    @Override
    public List<String> disconnected() {
        return devices.values().stream().filter(device -> {
            LocalTime updated = ofInstant(ofEpochMilli(device.getLastUpdate().getTime()), ZoneId.systemDefault()).toLocalTime();
            return updated.plus(disconnectionPeriod, ChronoUnit.MILLIS).isBefore(LocalTime.now());
        }).map(Device::getDeviceId).collect(toList());
    }

    @Override
    public void register(Device device) {
        if(isBlank(device.getRegistrationId())) {
            device.setRegistrationId(randomUUID().toString());
        }
        devices.put(device.getDeviceId(), device);
    }

    @Override
    public void update(Device device) {
        devices.put(device.getDeviceId(), device);
    }

    @Override
    public void deregister(String registrationId) {
        Device device = getByRegistrationId(registrationId);
        if (device != null) {
            devices.remove(device.getDeviceId());
        }
    }

    @Override
    public void heartbeat(String deviceId) {
        devices.get(deviceId).setLastUpdate(new Date());
    }

}
