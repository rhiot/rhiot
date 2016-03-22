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

import io.rhiot.cloudplatform.service.device.api.Device;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class InMemoryDeviceRegistry extends DisconnectionAwareDeviceRegistry {

    private final Map<String, Device> devices = new ConcurrentHashMap<>();

    public InMemoryDeviceRegistry(long disconnectionPeriod) {
        super(disconnectionPeriod);
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
    public void register(Device device) {
        if(device.getLastUpdate() == null) {
            device.setLastUpdate(new Date());
        }
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
    public void deregister(String deviceId) {
        Device device = get(deviceId);
        if (device != null) {
            devices.remove(device.getDeviceId());
        }
    }

    @Override
    public void heartbeat(String deviceId) {
        devices.get(deviceId).setLastUpdate(new Date());
    }

}
