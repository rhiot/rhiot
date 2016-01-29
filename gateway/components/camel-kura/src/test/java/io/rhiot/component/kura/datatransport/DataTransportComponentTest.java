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
package io.rhiot.component.kura.datatransport;

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.data.DataTransportService;
import org.junit.Test;

import static io.rhiot.component.kura.datatransport.KuraDataTransportConstants.CAMEL_KURA_DATATRANSPORT_QOS;
import static io.rhiot.component.kura.datatransport.KuraDataTransportConstants.DEFAULT_QOS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DataTransportComponentTest extends CamelTestSupport {

    DataTransportService dataTransportService = mock(DataTransportService.class);

    byte[] payload = "foo".getBytes();

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("dataTransportService", dataTransportService);
        return registry;
    }

    // Tests

    @Test
    public void shouldSendPayload() throws KuraException {
        sendBody("kura-datatransport:topic", payload);
        verify(dataTransportService).publish(anyString(), eq(payload), anyInt(), anyBoolean());
    }

    @Test
    public void shouldSendWithDefaultQos() throws KuraException {
        sendBody("kura-datatransport:topic", payload);
        verify(dataTransportService).publish(anyString(), any(byte[].class), eq(DEFAULT_QOS), anyBoolean());
    }

    @Test
    public void shouldUseQosFromHeader() throws KuraException {
        int qos = 2;
        template.sendBodyAndHeader("kura-datatransport:topic", payload, CAMEL_KURA_DATATRANSPORT_QOS, qos);
        verify(dataTransportService).publish(anyString(), any(byte[].class), eq(qos), anyBoolean());
    }

}