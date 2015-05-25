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
package com.github.camellabs.iot.component.grape

import org.apache.camel.CamelContext
import org.apache.camel.ServiceStatus
import org.apache.camel.impl.DefaultCamelContext
import org.junit.Assert
import org.junit.Test

import static org.apache.camel.ServiceStatus.Stopped

class GrapeComponentTest extends Assert {

    @Test
    void shouldLoadStreamComponent() {
        CamelContext camelContext = new DefaultCamelContext()
        camelContext.setApplicationContextClassLoader(new GroovyClassLoader())
        camelContext.start()
        camelContext.createProducerTemplate().sendBody('grape:org.apache.camel/camel-stream/2.15.2', 'msg')
        camelContext.createProducerTemplate().sendBody('stream:out', 'msg')
    }

    @Test
    void shouldLoadStreamComponentViaBodyRequest() {
        CamelContext camelContext = new DefaultCamelContext()
        camelContext.setApplicationContextClassLoader(new GroovyClassLoader())
        camelContext.start()
        camelContext.createProducerTemplate().sendBody('grape:grape', 'org.apache.camel/camel-stream/2.15.2')
        camelContext.createProducerTemplate().sendBody('stream:out', 'msg')
    }

    @Test
    void shouldLoadBeanAtRuntime() {
        CamelContext camelContext = new DefaultCamelContext()
        camelContext.setApplicationContextClassLoader(new GroovyClassLoader())
        camelContext.start()
        camelContext.createProducerTemplate().sendBody('grape:grape', 'org.apache.camel/camel-stream/2.15.2')
        def status = camelContext.createProducerTemplate().requestBody('bean:org.apache.camel.component.stream.StreamComponent?method=getStatus', null, ServiceStatus.class)
        assertEquals(Stopped, status)
    }

}
