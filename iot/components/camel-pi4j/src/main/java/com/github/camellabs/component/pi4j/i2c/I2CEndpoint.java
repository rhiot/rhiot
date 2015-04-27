/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.component.pi4j.i2c;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;

/**
 * Represents a I2C endpoint.
 */
@UriEndpoint(scheme = "pi4j-i2c", syntax = "pi4j-i2c://busId/deviceId", consumerClass = I2CConsumer.class, label = "iot", title = "i2c")
public class I2CEndpoint extends DefaultEndpoint {
    @UriPath
    @Metadata(required = "true")
    private String name;

    private int busId;

    private int deviceId;

    private I2CDevice device;

    private I2CBus bus;

    private int address;

    private I2CReadAction readAction;

    private int size = -1;

    private int offset = -1;

    private int bufferSize = -1;

    public I2CEndpoint(String uri, String remaining, I2CBus bus) {
        super(uri);
        this.bus = bus;
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        device = bus.getDevice(deviceId);
        return new I2CConsumer(this, processor, device);
    }

    public Producer createProducer() throws Exception {
        device = bus.getDevice(deviceId);
        return new I2CProducer(this, device);
    }

    public int getAddress() {
        return address;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public I2CBus getBus() {
        return bus;
    }

    public int getBusId() {
        return busId;
    }

    public I2CDevice getDevice() {
        return device;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }

    public I2CReadAction getReadAction() {
        return readAction;
    }

    public int getSize() {
        return size;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setBus(I2CBus bus) {
        this.bus = bus;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public void setDevice(I2CDevice device) {
        this.device = device;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setReadAction(I2CReadAction readAction) {
        this.readAction = readAction;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
