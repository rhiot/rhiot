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
package io.rhiot.steroids.camel

import org.apache.camel.component.mock.MockEndpoint
import org.junit.Test

import static io.rhiot.steroids.camel.CamelBootInitializer.camelContext

class CamelBootstrapTest {

    @Test
    void shouldStartApplicationProgramatically() {
        def app = new TestCamelApp()
        try {
            app.start()
            def mock = camelContext().getEndpoint('mock:camelApp', MockEndpoint.class)
            mock.reset()
            mock.expectedBodiesReceived('msg')
            camelContext().createProducerTemplate().sendBody('direct:camelApp', 'msg')
            mock.assertIsSatisfied()
        } finally {
            app.stop()
        }
    }

    @Test
    void shouldStartApplicationFromMain() {
        TestCamelApp.main()
        def mock = camelContext().getEndpoint('mock:camelApp', MockEndpoint.class)
        mock.reset()
        mock.expectedBodiesReceived('msg')
        camelContext().createProducerTemplate().sendBody('direct:camelApp', 'msg')
        mock.assertIsSatisfied()
    }

}

class TestCamelApp extends CamelBootstrap {

    @Override
    void configure() {
        from('direct:camelApp').to('mock:camelApp')
    }

}