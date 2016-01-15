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
package io.rhiot.cloudplatform.schema.device;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;

public class Device {

    private String deviceId;

    private String registrationId;

    private Date registrationDate;

    private Date lastUpdate;

    private InetAddress address;

    private int port;

    private InetSocketAddress registrationEndpointAddress;

    private long lifeTimeInSec;

    private String smsNumber;

    private String lwM2mVersion;

    private BindingMode bindingMode;

    // Constructors

    public Device() {
    }

    public Device(String deviceId, String registrationId, Date registrationDate, Date lastUpdate,
                  InetAddress address, int port, InetSocketAddress registrationEndpointAddress,
                  long lifeTimeInSec, String smsNumber, String lwM2mVersion, BindingMode bindingMode) {
        this.deviceId = deviceId;
        this.registrationId = registrationId;
        this.registrationDate = registrationDate;
        this.lastUpdate = lastUpdate;
        this.address = address;
        this.port = port;
        this.registrationEndpointAddress = registrationEndpointAddress;
        this.lifeTimeInSec = lifeTimeInSec;
        this.smsNumber = smsNumber;
        this.lwM2mVersion = lwM2mVersion;
        this.bindingMode = bindingMode;
    }

    // Getters and setters

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

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
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

    public String getSmsNumber() {
        return smsNumber;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    public String getLwM2mVersion() {
        return lwM2mVersion;
    }

    public void setLwM2mVersion(String lwM2mVersion) {
        this.lwM2mVersion = lwM2mVersion;
    }

    public BindingMode getBindingMode() {
        return bindingMode;
    }

    public void setBindingMode(BindingMode bindingMode) {
        this.bindingMode = bindingMode;
    }

    public static enum BindingMode {
        U,
        UQ,
        S,
        SQ,
        US,
        UQS;
    }

}