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
package io.rhiot.component.kura.cloud;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudService;
import org.eclipse.kura.message.KuraPayload;
import org.junit.Before;
import org.junit.Test;

public class KuraCloudProducerTestDeviceId extends CamelTestSupport {

    CloudService cloudService = mock(CloudService.class);
    CloudClient cloudClient;

    @Override
    protected void doPreSetup() throws Exception {
        given(cloudService.newCloudClient(anyString())).willReturn(mock(CloudClient.class));
        cloudClient = KuraCloudComponent.clientCache().getOrCreate("app", cloudService);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("cloudService", cloudService);
        return registry;
    }

    @Test
    public void shouldSendKuraPayloadToTopicURLDeviceId() throws KuraException {
        // Given
        KuraPayload kuraPayload = new KuraPayload();

        // When
        template.sendBody("kura-cloud:app/topic?deviceId=SerialNumber-XYZ&control=true", kuraPayload);

        // Then
        verify(cloudClient).controlPublish(eq("SerialNumber-XYZ"), eq("topic"), eq(kuraPayload), anyInt(), anyBoolean(),
                anyInt());

    }

    @Test
    public void shouldSendKuraPayloadToTopicHeaderDeviceId() throws KuraException {
        // Given
        KuraPayload kuraPayload = new KuraPayload();

        // When
        template.sendBodyAndHeader("kura-cloud:app/topic?control=true", kuraPayload,
                KuraCloudConstants.CAMEL_KURA_CLOUD_DEVICEID, "SerialNumber-ABC");

        // Then
        verify(cloudClient).controlPublish(eq("SerialNumber-ABC"), eq("topic"), eq(kuraPayload), anyInt(), anyBoolean(),
                anyInt());

    }

}