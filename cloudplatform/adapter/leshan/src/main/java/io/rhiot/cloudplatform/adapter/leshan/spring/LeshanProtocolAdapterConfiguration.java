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
package io.rhiot.cloudplatform.adapter.leshan.spring;

import io.rhiot.cloudplatform.adapter.leshan.IoTConnectorClientRegistry;
import io.rhiot.cloudplatform.adapter.leshan.Lwm2mMetricResolver;
import io.rhiot.cloudplatform.adapter.leshan.LeshanDeviceMetricsPollService;
import io.rhiot.cloudplatform.adapter.leshan.MetricResolver;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.cloudplatform.service.binding.ServiceBinding;
import io.rhiot.cloudplatform.connector.IoTConnector;
import org.eclipse.leshan.server.californium.LeshanServerBuilder;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.client.ClientRegistry;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class LeshanProtocolAdapterConfiguration {

    private static final Logger LOG = getLogger(LeshanProtocolAdapterConfiguration.class);

    @Bean
    ClientRegistry ioTConnectorClientRegistry(IoTConnector connector) {
        return new IoTConnectorClientRegistry(connector);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    LeshanServer leshanServer(ClientRegistry clientRegistry, @Value("${leshan.port:5683}") int port) {
        LeshanServerBuilder leshanServerBuilder = new LeshanServerBuilder();
        LOG.debug("Creating Leshan server using port {}.", port);
        leshanServerBuilder.setLocalAddress("0.0.0.0", port);
        return leshanServerBuilder.setClientRegistry(clientRegistry).build();
    }

    @Bean(name = "deviceMetricsPoll")
    LeshanDeviceMetricsPollService leshanDeviceMetricsPollService(LeshanServer leshanServer, MetricResolver metricResolver) {
        return new LeshanDeviceMetricsPollService(leshanServer, metricResolver);
    }

    @Bean
    ServiceBinding leshanServiceBinding(PayloadEncoding payloadEncoding) {
        return new ServiceBinding(payloadEncoding, "deviceMetricsPoll");
    }

    @Bean
    MetricResolver metricResolver() {
        return new Lwm2mMetricResolver();
    }

}
