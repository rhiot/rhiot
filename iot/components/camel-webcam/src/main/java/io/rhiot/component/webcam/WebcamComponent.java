/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.component.webcam;

import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.UriEndpointComponent;

/**
 * Represents the component that manages {@link WebcamEndpoint}.
 */
public class WebcamComponent extends UriEndpointComponent {
    
    public WebcamComponent() {
        super(WebcamEndpoint.class);
    }

    public WebcamComponent(CamelContext context) {
        super(context, WebcamEndpoint.class);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new WebcamEndpoint(uri, this);
        setProperties(endpoint, parameters);

        return endpoint;
    }
    // Life-cycle

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }
}
