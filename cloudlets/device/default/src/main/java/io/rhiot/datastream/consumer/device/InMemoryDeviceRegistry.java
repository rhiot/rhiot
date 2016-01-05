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
package io.rhiot.datastream.consumer.device;

import io.rhiot.datastream.schema.device.Device;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static io.rhiot.utils.Uuids.uuid;
import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class InMemoryDeviceRegistry implements DeviceRegistry {

    Map<String, Device> devices = new HashMap<>();

    @Override
    public Device get(String deviceId) {
        return devices.get(deviceId);
    }

    @Override
    public List<Device> list() {
        return new ArrayList<>(devices.values());
    }

    @Override
    public List<String> disconnected() {
        return devices.values().stream().filter(device -> {
            LocalTime updated = ofInstant(ofEpochMilli(device.getLastUpdate().getTime()), ZoneId.systemDefault()).toLocalTime();
            return updated.plus(MINUTES.toMillis(1), ChronoUnit.MILLIS).isBefore(LocalTime.now());
        }).map(Device::getDeviceId).collect(toList());
    }

    @Override
    public void register(Device device) {
        if(isBlank(device.getRegistrationId())) {
            device.setRegistrationId(uuid());
        }
        devices.put(device.getDeviceId(), device);
    }

    @Override
    public void deregister(String registrationId) {
        List<Device> matchingDevices = devices.values().stream().filter(device -> registrationId.equals(device.getRegistrationId())).collect(toList());
        devices.remove(matchingDevices.get(0).getDeviceId());
    }

}
