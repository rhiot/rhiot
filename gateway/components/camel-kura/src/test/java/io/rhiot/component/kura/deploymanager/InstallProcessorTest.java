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

import static com.google.common.io.Files.createTempDir;

public class InstallProcessorTest extends CamelTestSupport {

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
                System.setProperty("kura_deploy_directory", deployDirectory.getAbsolutePath());
                from("direct:test").process(new InstallProcessor());
            }
        };
    }

    @Test
    public void shouldInstallBundle() {
        template.sendBodyAndHeader("direct:test", "someBundleData".getBytes(), "DeployManager.topic", "deploymanager/install/bundleName");
        Truth.assertThat(deployDirectory.list()).asList().contains("bundleName.jar");
    }

}
