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
package io.rhiot.component.kura.test;

import io.rhiot.component.kura.router.RhiotKuraRouter;
import io.rhiot.component.kura.test.TestKuraServer;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kura.KuraRouter;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Ignore;
import org.junit.Test;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;

@Ignore
public class TestKuraServerTest {

    @Test
    public void shouldStartRoute() throws InterruptedException {
        // Given
        TestKuraServer kuraServer = new TestKuraServer();
        KuraRouter router = kuraServer.start(TestKuraRouter.class);
        MockEndpoint mock = router.getContext().getEndpoint("mock:out", MockEndpoint.class);
        mock.expectedMessageCount(1);
        ProducerTemplate template = router.getContext().createProducerTemplate();

        // When
        template.sendBody("direct:start", "This is a test message");

        // Then
        assertIsSatisfied(mock);
    }

    static class TestKuraRouter extends RhiotKuraRouter {

        @Override
        public void configure() throws Exception {
            from("direct:start").to("log:org.apache.camel.component.kura.TestKuraRouter").to("mock:out");
        }

    }

}