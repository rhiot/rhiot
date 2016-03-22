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
package io.rhiot.cloudplatform.adapter.leshan;

import io.rhiot.cloudplatform.service.device.api.DeviceMetricsPollService;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.response.ValueResponse;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.Client;

public class LeshanDeviceMetricsPollService implements DeviceMetricsPollService {

    private final LeshanServer leshanServer;

    private final MetricResolver metricResolver;

    public LeshanDeviceMetricsPollService(LeshanServer leshanServer, MetricResolver metricResolver) {
        this.leshanServer = leshanServer;
        this.metricResolver = metricResolver;
    }

    // Overridden

    @Override
    public Object read(String deviceId, String metric) {
        String translatedMetric = metricResolver.resolveMetric(metric);

        Client client = leshanServer.getClientRegistry().get(deviceId);
        ValueResponse leshanResponse = leshanServer.send(client, new ReadRequest(translatedMetric), 5000);
        if(leshanResponse == null) {
            return null;
        }
        return  ((LwM2mResource) leshanResponse.getContent()).getValue().value;
    }

}
