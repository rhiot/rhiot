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

import static com.google.common.truth.Truth.assertThat;
import static org.eclipse.kura.core.util.IOUtil.readResource;

import io.rhiot.component.kura.test.TestKuraServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

@Ignore
public class RhiotKuraRouterTest {

    TestKuraServer kuraServer = new TestKuraServer();

    @Test
    public void shouldHandleNoRoute() throws IOException {
        // Given
        MyRouter router = kuraServer.start(MyRouter.class);

        // When
        router.updated(new HashMap<String, Object>());

        // Then
        assertThat(router.getContext().getRouteDefinitions()).hasSize(0);
    }

    @Test
    public void shouldLoadScrRoutes() throws IOException {
        // Given
        Map<String, Object> scrProperties = ImmutableMap.<String, Object> of(RhiotKuraConstants.XML_ROUTE_PROPERTY,
                readResource("routes.xml"));
        MyRouter router = kuraServer.start(MyRouter.class);

        // When
        router.updated(scrProperties);

        // Then
        assertThat(router.getContext().getRouteDefinitions()).hasSize(1);
    }

    @Test
    public void shouldReloadScrRoutes() throws IOException {
        // Given
        Map<String, Object> scrProperties = ImmutableMap.<String, Object> of(RhiotKuraConstants.XML_ROUTE_PROPERTY,
                readResource("routes.xml"));
        MyRouter router = kuraServer.start(MyRouter.class);

        // When
        router.updated(scrProperties);
        router.updated(scrProperties);

        // Then
        assertThat(router.getContext().getRouteDefinitions()).hasSize(1);
    }

    @Test
    public void shouldAddMoreScrRoutes() throws IOException {
        // Given
        Map<String, Object> scrProperties = ImmutableMap.<String, Object> of(RhiotKuraConstants.XML_ROUTE_PROPERTY,
                readResource("routes.xml"));
        Map<String, Object> newScrProperties = ImmutableMap.<String, Object> of(RhiotKuraConstants.XML_ROUTE_PROPERTY,
                readResource("routes-new.xml"));
        MyRouter router = kuraServer.start(MyRouter.class);

        // When
        router.updated(scrProperties);
        router.updated(newScrProperties);

        // Then
        assertThat(router.getContext().getRouteDefinitions()).hasSize(2);
    }

    // Fixture classes

    public static class MyRouter extends RhiotKuraRouter {
    }

}
