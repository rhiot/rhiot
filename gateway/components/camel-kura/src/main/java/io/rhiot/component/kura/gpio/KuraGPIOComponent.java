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
package io.rhiot.component.kura.gpio;

import io.rhiot.component.kura.utils.KuraServiceFactory;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.eclipse.kura.gpio.GPIOService;

public class KuraGPIOComponent extends UriEndpointComponent {

    public KuraGPIOComponent() {
        super(KuraGPIOEndpoint.class);
    }

    public KuraGPIOComponent(CamelContext context, Class<? extends Endpoint> endpointClass) {
        super(context, endpointClass);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remain, Map<String, Object> parameters) throws Exception {
        GPIOService service = KuraServiceFactory.retrieveService(GPIOService.class, this.getCamelContext().getRegistry());
        return new KuraGPIOEndpoint(uri, this, service, remain);
    }

}
