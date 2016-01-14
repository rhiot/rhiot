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
import org.eclipse.leshan.core.node.LwM2mResource
import org.eclipse.leshan.core.response.LwM2mResponse

import static io.rhiot.utils.Uuids.uuid

class VirtualDevice extends GenericDevice {

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
                return Object.write(resourceid, value);
        }
    }

    @Override
    String manufacturer() {
        'Rhiot'
    }

    @Override
    String modelNumber() {
        'Virtual device'
    }

    @Override
    String serialNumber() {
        "Serial-${uuid()}"
    }

    @Override
    String firmwareVersion() {
        '1.0.0'
    }

}