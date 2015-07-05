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
package com.github.camellabs.iot.cloudlet.device

import org.eclipse.leshan.ResponseCode
import org.eclipse.leshan.client.resource.BaseInstanceEnabler
import org.eclipse.leshan.core.node.LwM2mResource
import org.eclipse.leshan.core.node.Value
import org.eclipse.leshan.core.response.LwM2mResponse
import org.eclipse.leshan.core.response.ValueResponse

import java.text.SimpleDateFormat

class GenericDevice extends BaseInstanceEnabler {

    public Device() {
        // notify new date each 5 second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fireResourceChange(13);
            }
        }, 5000, 5000);
    }

    @Override
    public ValueResponse read(int resourceid) {
        System.out.println("Read on Device Resource " + resourceid);
        switch (resourceid) {
            case 0:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newStringValue(getManufacturer())));
            case 1:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newStringValue(getModelNumber())));
            case 2:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newStringValue(getSerialNumber())));
            case 3:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newStringValue(getFirmwareVersion())));
            case 9:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newIntegerValue(getBatteryLevel())));
            case 10:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newIntegerValue(getMemoryFree())));
            case 11:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        [Value.newIntegerValue(getErrorCode())]));
            case 13:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newDateValue(getCurrentTime())));
            case 14:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newStringValue(getUtcOffset())));
            case 15:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newStringValue(getTimezone())));
            case 16:
                return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newStringValue(getSupportedBinding())));
            default:
                return super.read(resourceid);
        }
    }

    @Override
    public LwM2mResponse execute(int resourceid, byte[] params) {
        System.out.println("Execute on Device resource " + resourceid);
        if (params != null && params.length != 0)
            System.out.println("\t params " + new String(params));
        return new LwM2mResponse(ResponseCode.CHANGED);
    }

    @Override
    public LwM2mResponse write(int resourceid, LwM2mResource value) {
        System.out.println("Write on Device Resource " + resourceid + " value " + value);
        switch (resourceid) {
            case 13:
                return new LwM2mResponse(ResponseCode.NOT_FOUND);
            case 14:
                setUtcOffset((String) value.getValue().value);
                fireResourceChange(resourceid);
                return new LwM2mResponse(ResponseCode.CHANGED);
            case 15:
                setTimezone((String) value.getValue().value);
                fireResourceChange(resourceid);
                return new LwM2mResponse(ResponseCode.CHANGED);
            default:
                return super.write(resourceid, value);
        }
    }

    private String getManufacturer() {
        return "Leshan Example Device";
    }

    private String getModelNumber() {
        return "Model 500";
    }

    private String getSerialNumber() {
        return "LT-500-000-0001";
    }

    private String getFirmwareVersion() {
        return "1.0.0";
    }

    private int getErrorCode() {
        return 0;
    }

    private int getBatteryLevel() {
        final Random rand = new Random();
        return rand.nextInt(100);
    }

    private int getMemoryFree() {
        final Random rand = new Random();
        return rand.nextInt(50) + 114;
    }

    private Date getCurrentTime() {
        return new Date();
    }

    private String utcOffset = new SimpleDateFormat("X").format(Calendar.getInstance().getTime());;

    private String getUtcOffset() {
        return utcOffset;
    }

    private void setUtcOffset(String t) {
        utcOffset = t;
    }

    private String timeZone = TimeZone.getDefault().getID();

    private String getTimezone() {
        return timeZone;
    }

    private void setTimezone(String t) {
        timeZone = t;
    }

    private String getSupportedBinding() {
        return "U";
    }
}

