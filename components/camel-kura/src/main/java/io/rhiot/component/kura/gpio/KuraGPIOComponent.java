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

import java.util.Map;
import java.util.Set;

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

        Set<GPIOService> serviceSet = this.getCamelContext().getRegistry().findByType(GPIOService.class);
        GPIOService service = null;

        int serviceCount = serviceSet.size();
        if (serviceCount == 1) {
            for (GPIOService serviceIt : serviceSet) {
                service = serviceIt;
            }
        } else if (serviceCount > 1) {
            throw new IllegalStateException("Too many GPIOService services found in a registry: " + serviceCount);
        } else {
            throw new IllegalArgumentException("No GPIOService service instance found in a registry.");
        }

        KuraGPIOEndpoint ret = new KuraGPIOEndpoint(uri, this, service);

        parameters.put(KuraGPIOConstants.CAMEL_KURA_GPIO_ATTR_NAME_GPIOID, remain);
        setProperties(ret, parameters);

        return ret;
    }

}
