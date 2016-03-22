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
import io.rhiot.cloudplatform.service.device.api.DeviceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.util.stream.Collectors.toList;

public abstract class DisconnectionAwareDeviceRegistry implements DeviceRegistry {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected final long disconnectionPeriod;

    public DisconnectionAwareDeviceRegistry(long disconnectionPeriod) {
        this.disconnectionPeriod = disconnectionPeriod;
    }

    @Override
    public List<String> disconnected() {
        return list().stream().filter(device -> {
            LocalTime updated = ofInstant(ofEpochMilli(device.getLastUpdate().getTime()), ZoneId.systemDefault()).toLocalTime();
            return updated.plus(disconnectionPeriod, ChronoUnit.MILLIS).isBefore(LocalTime.now());
        }).map(Device::getDeviceId).collect(toList());
    }

}
