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
package io.rhiot.cloudplatform.camel.openalpr;

import io.rhiot.utils.process.ProcessManager;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;

import java.io.File;
import java.util.Map;

public class OpenalprComponent extends UriEndpointComponent {

    private String country;

    private File workDir;

    private ProcessManager processManager;

    private String dockerImage;

    private OpenalprCommandStrategy commandStrategy = new DockerizedOpenalprCommandStrategy();

    public OpenalprComponent() {
        super(OpenalprEndpoint.class);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new OpenalprEndpoint(uri, this);
    }

    // Getters and setters

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public File getWorkDir() {
        return workDir;
    }

    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public OpenalprCommandStrategy getCommandStrategy() {
        return commandStrategy;
    }

    public void setCommandStrategy(OpenalprCommandStrategy commandStrategy) {
        this.commandStrategy = commandStrategy;
    }

}