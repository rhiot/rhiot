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
package com.github.camellabs.iot.component.gps.bu353;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import java.util.Set;

public class GpsBu353Endpoint extends DefaultEndpoint {

    GpsCoordinatesSource gpsCoordinatesSource;

    public GpsBu353Endpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return null;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new GpsBu353Consumer(this, processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        if(gpsCoordinatesSource == null) {
            Set<GpsCoordinatesSource> sources = getCamelContext().getRegistry().findByType(GpsCoordinatesSource.class);
            if(sources.size() == 1) {
                gpsCoordinatesSource = sources.iterator().next();
            } else {
                gpsCoordinatesSource = new SerialGpsCoordinatesSource();
            }
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public GpsCoordinatesSource getGpsCoordinatesSource() {
        return gpsCoordinatesSource;
    }

    public void setGpsCoordinatesSource(GpsCoordinatesSource gpsCoordinatesSource) {
        this.gpsCoordinatesSource = gpsCoordinatesSource;
    }

}
