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

import io.rhiot.utils.process.DefaultProcessManager;
import io.rhiot.utils.process.ProcessManager;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import java.io.File;

public class OpenalprEndpoint extends DefaultEndpoint {

    private String country = "eu";

    private File workDir = new File("/tmp/openalpr-workdir");

    private ProcessManager processManager = new DefaultProcessManager();

    private String dockerImage = "rhiot/openalpr:0.1.4-SNAPSHOT";

    private OpenalprCommandStrategy commandStrategy = new DockerizedOpenalprCommandStrategy();

    public OpenalprEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        getWorkDir().mkdirs();
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OpenalprProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Openalpr component supports only producer endpoints.");
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    // Getters and setters


    @Override
    public OpenalprComponent getComponent() {
        return (OpenalprComponent) super.getComponent();
    }

    public String getCountry() {
        if(getComponent().getCountry() != null) {
            return getComponent().getCountry();
        }
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public File getWorkDir() {
        if(getComponent().getWorkDir() != null) {
            return getComponent().getWorkDir();
        }
        return workDir;
    }

    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    public ProcessManager getProcessManager() {
        if(getComponent().getProcessManager() != null) {
            return getComponent().getProcessManager();
        }
        return processManager;
    }

    public void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public String getDockerImage() {
        if(getComponent().getDockerImage() != null) {
            return getComponent().getDockerImage();
        }
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public OpenalprCommandStrategy getCommandStrategy() {
        if(getComponent().getCommandStrategy() != null) {
            return getComponent().getCommandStrategy();
        }
        return commandStrategy;
    }

    public void setCommandStrategy(OpenalprCommandStrategy commandStrategy) {
        this.commandStrategy = commandStrategy;
    }

}