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

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Represents information about device registered in a cloud service.
 */
public class Device {

    private String id;

    private String deviceId;

    private String registrationId;

    private Date registrationDate;

    private Date lastUpdate;

    private String address;

    private int port;

    private InetSocketAddress registrationEndpointAddress;

    private long lifeTimeInSec;

    private List<LinkObject> objectLinks = new LinkedList<>();

    private Map<String, Object> properties = new HashMap<>();

    // Constructors

    public Device() {
    }

    public Device(String id, String deviceId, String registrationId, Date registrationDate, Date lastUpdate, String address, int port, InetSocketAddress registrationEndpointAddress, long lifeTimeInSec, List<LinkObject> objectLinks, Map<String, Object> properties) {
        this.id = id;
        this.deviceId = deviceId;
        this.registrationId = registrationId;
        this.registrationDate = registrationDate;
        this.lastUpdate = lastUpdate;
        this.address = address;
        this.port = port;
        this.registrationEndpointAddress = registrationEndpointAddress;
        this.lifeTimeInSec = lifeTimeInSec;
        this.objectLinks = objectLinks;
        this.properties = properties;
    }

    public static Device minimalDevice(String deviceId) {
        Device device = new Device();
        device.setDeviceId(deviceId);
        return device;
    }

    // Getters and setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetSocketAddress getRegistrationEndpointAddress() {
        return registrationEndpointAddress;
    }

    public void setRegistrationEndpointAddress(InetSocketAddress registrationEndpointAddress) {
        this.registrationEndpointAddress = registrationEndpointAddress;
    }

    public long getLifeTimeInSec() {
        return lifeTimeInSec;
    }

    public void setLifeTimeInSec(long lifeTimeInSec) {
        this.lifeTimeInSec = lifeTimeInSec;
    }

    public List<LinkObject> getObjectLinks() {
        return objectLinks;
    }

    public void setObjectLinks(List<LinkObject> objectLinks) {
        this.objectLinks = objectLinks;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

}