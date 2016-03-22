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

import java.util.List;

/**
 * Backend service used to manage devices connecting to the system.
 */
public interface DeviceRegistry {

    Device get(String deviceId);

    Device getByRegistrationId(String registrationId);

    List<Device> list();

    List<String> disconnected();

    /**
     * Registers a given device. Registering device with the same `deviceId` twice should update the device, instead
     * of registering two devices.
     *
     * @param device device to be registered.
     */
    void register(Device device);

    void update(Device device);

    void deregister(String deviceId);

    void heartbeat(String deviceId);

}
