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

class DeviceDetail {

    private static def detailsMatrix = [
            manufacturer: '/3/0/0', modelNumber: '/3/0/1', serialNumber: '/3/0/2', firmwareVersion: '/3/0/3']

    private final String metric

    private final String resource

    private DeviceDetail(String metric, String resource) {
        this.metric = metric
        this.resource = resource
    }

    static def allDeviceDetails() {
        detailsMatrix.collect{ new DeviceDetail(it.key, it.value) }
    }

    static def manufacturer() {
        new DeviceDetail('manufacturer', detailsMatrix.manufacturer)
    }

    static def modelNumber() {
        new DeviceDetail('modelNumber', detailsMatrix.modelNumber)
    }

    static def serialNumber() {
        new DeviceDetail('serialNumber', detailsMatrix.serialNumber)
    }

    static def firmwareVersion() {
        new DeviceDetail('firmwareVersion', detailsMatrix.firmwareVersion)
    }

    def metric() {
        metric
    }

    def resource() {
        resource
    }

}
