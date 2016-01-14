package io.rhiot.utils.leshan.client
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


import org.eclipse.leshan.ResponseCode
import org.eclipse.leshan.client.resource.BaseInstanceEnabler
import org.eclipse.leshan.core.node.LwM2mResource
import org.eclipse.leshan.core.node.Value
import org.eclipse.leshan.core.response.LwM2mResponse
import org.eclipse.leshan.core.response.ValueResponse

import java.text.SimpleDateFormat

import static org.eclipse.leshan.ResponseCode.CONTENT
import static org.eclipse.leshan.core.node.Value.newStringValue

/**
 * Generic device object returning fixed values. Can be used as a base for the implementation of the  particular devices.
 */
class GenericDevice extends BaseInstanceEnabler {

    @Override
    public ValueResponse read(int resourceid) {
        switch (resourceid) {
            case 0: return generateValueResponse(resourceid, manufacturer())
            case 1: return generateValueResponse(resourceid, modelNumber())
            case 2: return generateValueResponse(resourceid, serialNumber())
            case 3: return generateValueResponse(resourceid, firmwareVersion())
            case 9:
                return new ValueResponse(org.eclipse.leshan.ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newIntegerValue(getBatteryLevel())));
            case 10:
                return new ValueResponse(org.eclipse.leshan.ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newIntegerValue(getMemoryFree())));
            case 11:
                return new ValueResponse(org.eclipse.leshan.ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        [Value.newIntegerValue(getErrorCode())]));
            case 13:
                return new ValueResponse(org.eclipse.leshan.ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        Value.newDateValue(getCurrentTime())));
            case 14:
                return new ValueResponse(org.eclipse.leshan.ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        org.eclipse.leshan.core.node.Value.newStringValue(getUtcOffset())));
            case 15:
                return new ValueResponse(org.eclipse.leshan.ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        org.eclipse.leshan.core.node.Value.newStringValue(getTimezone())));
            case 16:
                return new ValueResponse(org.eclipse.leshan.ResponseCode.CONTENT, new LwM2mResource(resourceid,
                        org.eclipse.leshan.core.node.Value.newStringValue(getSupportedBinding())));
            default:
                return super.read(resourceid);
        }
    }

    @Override
    public LwM2mResponse execute(int resourceid, byte[] params) {
        return new LwM2mResponse(ResponseCode.CHANGED);
    }

    @Override
    public LwM2mResponse write(int resourceid, LwM2mResource value) {
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

    protected ValueResponse generateValueResponse(int resourceid, String value) {
        if(value == null) {
            return super.read(resourceid)
        }
        new ValueResponse(org.eclipse.leshan.ResponseCode.CONTENT, new LwM2mResource(resourceid, org.eclipse.leshan.core.node.Value.newStringValue(value)))
    }

    // Device parameters callbacks

    String manufacturer() {
        'Generic manufacturer'
    }

    String modelNumber() {
        'Generic model number'
    }

    String serialNumber() {
        'Generic serial number'
    }

    String firmwareVersion() {
        '1.0.0'
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

