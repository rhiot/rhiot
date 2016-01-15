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


import org.eclipse.leshan.LinkObject
import org.eclipse.leshan.core.request.BindingMode
import org.eclipse.leshan.core.request.UpdateRequest

class UpdateRequestBuilder {

    private InetAddress address;
   
    private Integer port;
   
    private Long lifeTimeInSec;
   
    private String smsNumber;
   
    private BindingMode bindingMode;
   
    private LinkObject[] objectLinks;

    UpdateRequest build(String registrationId) {
        new UpdateRequest(registrationId, address, port, lifeTimeInSec, smsNumber, bindingMode, objectLinks)
    }

    UpdateRequestBuilder address(InetAddress address) {
        this.address = address
        this
    }

    UpdateRequestBuilder port(Integer port) {
        this.port = port
        this
    }

    UpdateRequestBuilder lifeTimeInSec(Long lifeTimeInSec) {
        this.lifeTimeInSec = lifeTimeInSec
        this
    }

    UpdateRequestBuilder smsNumber(String smsNumber) {
        this.smsNumber = smsNumber
        this
    }

    UpdateRequestBuilder bindingMode(BindingMode bindingMode) {
        this.bindingMode = bindingMode
        this
    }

    UpdateRequestBuilder objectLinks(LinkObject[] objectLinks) {
        this.objectLinks = objectLinks
        this
    }

}