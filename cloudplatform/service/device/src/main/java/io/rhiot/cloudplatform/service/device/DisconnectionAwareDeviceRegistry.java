package io.rhiot.cloudplatform.service.device;

import org.eclipse.cloudplatform.service.device.api.Device;
import org.eclipse.cloudplatform.service.device.api.DeviceRegistry;
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
