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

import groovy.transform.Immutable
import org.eclipse.leshan.ResponseCode
import org.eclipse.leshan.client.californium.LeshanClient
import org.eclipse.leshan.client.resource.LwM2mObjectEnabler
import org.eclipse.leshan.client.resource.ObjectEnabler
import org.eclipse.leshan.client.resource.ObjectsInitializer
import org.eclipse.leshan.core.request.RegisterRequest
import org.eclipse.leshan.core.response.RegisterResponse
import org.springframework.util.SocketUtils

import static org.springframework.util.SocketUtils.findAvailableTcpPort

class LeshanCloudClient {

    String clientId

    String server

    LeshanClient leshanClient

    int clientPort = findAvailableTcpPort()

    static LeshanCloudClient createLeshanCloudClient(String clientId) {
        new LeshanCloudClient(clientId: clientId, server: 'localhost:5683')
    }

    LeshanCloudClient connect() {
        def initializer = new ObjectsInitializer()
        initializer.setClassForObject(3, GenericDevice.class)
        def enablers = initializer.createMandatory()
        enablers.addAll(initializer.create(6))

        def clientAddress = new InetSocketAddress('0.0.0.0', clientPort);
        def serverParts = server.split(/:/)
        def serverAddress = new InetSocketAddress(serverParts[0], serverParts[1].toInteger());

        leshanClient = new LeshanClient(clientAddress, serverAddress, enablers.asImmutable())

        leshanClient.start()

        def response = leshanClient.send(new RegisterRequest(clientId));

        // Report registration response.
        System.out.println("Device Registration (Success? " + response.getCode() + ")");
        if (response.getCode() == ResponseCode.CREATED) {
            System.out.println("\tDevice: Registered Client Location '" + response.getRegistrationID() + "'");
            String registrationID = response.getRegistrationID();
            println(">>>>>>>>>>>>>> " + registrationID)
        } else {
            // TODO Should we have a error message on response ?
            // System.err.println("\tDevice Registration Error: " + response.getErrorMessage());
            System.err.println("\tDevice Registration Error: " + response.getCode());
            System.err
                    .println("If you're having issues connecting to the LWM2M endpoint, try using the DTLS port instead");
        }
        this
    }

    LeshanCloudClient disconnect() {
        leshanClient.stop()
        this
    }

}
