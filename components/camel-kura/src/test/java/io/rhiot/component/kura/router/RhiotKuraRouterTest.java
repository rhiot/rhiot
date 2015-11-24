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
package io.rhiot.component.kura.router;

import io.rhiot.component.kura.test.TestKuraServer;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static org.eclipse.kura.core.util.IOUtil.readResource;

public class RhiotKuraRouterTest {

    TestKuraServer kuraServer = new TestKuraServer();

    @Test
    public void shouldLoadScrRoutes() throws IOException {
        MyRouter router = kuraServer.start(MyRouter.class);
        router.setCamelRouteXml(readResource("routes.xml"));
        assertThat(router.getContext().getRouteDefinitions()).hasSize(1);
    }

    @Test
    public void shouldReloadScrRoutes() throws IOException {
        MyRouter router = kuraServer.start(MyRouter.class);
        router.setCamelRouteXml(readResource("routes.xml"));
        router.setCamelRouteXml(readResource("routes.xml"));
        assertThat(router.getContext().getRouteDefinitions()).hasSize(1);
    }

    @Test
    public void shouldAddMoreScrRoutes() throws IOException {
        MyRouter router = kuraServer.start(MyRouter.class);
        router.setCamelRouteXml(readResource("routes.xml"));
        router.setCamelRouteXml(readResource("routes-new.xml"));
        assertThat(router.getContext().getRouteDefinitions()).hasSize(2);
    }

    public static class MyRouter extends RhiotKuraRouter {
        @Override
        public void configure() throws Exception {

        }
    }

}

