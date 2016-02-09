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
package io.rhiot.component.kura.deploymanager;

import com.google.common.truth.Truth;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.google.common.io.Files.createTempDir;
import static java.lang.System.setProperty;

public class UninstallProcessorTest extends CamelTestSupport {

    File deployDirectory;

    @Override
    protected void doPreSetup() throws Exception {
        deployDirectory = createTempDir();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                setProperty("kura_deploy_directory", deployDirectory.getAbsolutePath());
                from("direct:test").process(new UninstallProcessor());
            }
        };
    }

    // Tests

    @Test
    public void shouldUninstallBundle() throws IOException {
        // Given
        String bundleName = UUID.randomUUID().toString();
        File bundle = new File(deployDirectory, bundleName + ".jar");
        bundle.createNewFile();

        // When
        template.sendBody("direct:test", bundleName);

        // Then
        Truth.assertThat(bundle.exists()).isFalse();
    }

}
