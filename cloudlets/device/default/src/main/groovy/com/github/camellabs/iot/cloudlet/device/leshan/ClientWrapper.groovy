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
package com.github.camellabs.iot.cloudlet.device.leshan

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.leshan.LinkObject
import org.eclipse.leshan.core.request.BindingMode
import org.eclipse.leshan.server.client.Client

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES

class ClientWrapper {

    private static final def OBJECT_MAPPER = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    private Date registrationDate

    private InetAddress address

    private int port

    private InetSocketAddress registrationEndpointAddress

    private long lifeTimeInSec

    private String smsNumber

    private String lwM2mVersion

    private BindingMode bindingMode

    private String endpoint

    private String registrationId

    private String rootPath

    private Date lastUpdate

    private LinkObjectWrapper[] objectLinks

    static ClientWrapper clientWrapperFromMap(Map map) {
        OBJECT_MAPPER.convertValue(map, ClientWrapper.class)
    }

    Client toLeshanClient() {
        def links = objectLinks.collect { new LinkObject(it.url, it.attributes) } as LinkObject[]
        new Client(registrationId, endpoint, address, port, lwM2mVersion,
                lifeTimeInSec, smsNumber, bindingMode, links,
                registrationEndpointAddress, registrationDate, lastUpdate)
    }

    Date getRegistrationDate() {
        return registrationDate
    }

    void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate
    }

    InetAddress getAddress() {
        return address
    }

    void setAddress(InetAddress address) {
        this.address = address
    }

    int getPort() {
        return port
    }

    void setPort(int port) {
        this.port = port
    }

    InetSocketAddress getRegistrationEndpointAddress() {
        return registrationEndpointAddress
    }

    void setRegistrationEndpointAddress(InetSocketAddress registrationEndpointAddress) {
        this.registrationEndpointAddress = registrationEndpointAddress
    }

    long getLifeTimeInSec() {
        return lifeTimeInSec
    }

    void setLifeTimeInSec(long lifeTimeInSec) {
        this.lifeTimeInSec = lifeTimeInSec
    }

    String getSmsNumber() {
        return smsNumber
    }

    void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber
    }

    String getLwM2mVersion() {
        return lwM2mVersion
    }

    void setLwM2mVersion(String lwM2mVersion) {
        this.lwM2mVersion = lwM2mVersion
    }

    BindingMode getBindingMode() {
        return bindingMode
    }

    void setBindingMode(BindingMode bindingMode) {
        this.bindingMode = bindingMode
    }

    String getEndpoint() {
        return endpoint
    }

    void setEndpoint(String endpoint) {
        this.endpoint = endpoint
    }

    String getRegistrationId() {
        return registrationId
    }

    void setRegistrationId(String registrationId) {
        this.registrationId = registrationId
    }

    String getRootPath() {
        return rootPath
    }

    void setRootPath(String rootPath) {
        this.rootPath = rootPath
    }

    Date getLastUpdate() {
        return lastUpdate
    }

    void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate
    }

    LinkObjectWrapper[] getObjectLinks() {
        return objectLinks
    }

    void setObjectLinks(LinkObjectWrapper[] objectLinks) {
        this.objectLinks = objectLinks
    }

}