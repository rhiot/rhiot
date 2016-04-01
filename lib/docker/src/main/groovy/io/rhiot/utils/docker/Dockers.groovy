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
package io.rhiot.utils.docker

import com.google.common.collect.Sets
import com.spotify.docker.client.DefaultDockerClient
import com.spotify.docker.client.DockerClient
import com.spotify.docker.client.DockerException
import com.spotify.docker.client.ImageNotFoundException
import com.spotify.docker.client.messages.ContainerConfig
import com.spotify.docker.client.messages.ContainerCreation
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static java.lang.Runtime.getRuntime

@CompileStatic
public class Dockers {

    private static final Logger LOG = LoggerFactory.getLogger(Dockers.class);

    private final DockerClient docker

    private Set<String> startedContainers = Sets.newConcurrentHashSet();

    Dockers(DockerClient docker) {
        this.docker = docker
    }

    static Dockers dockers() {
        new Dockers(DefaultDockerClient.fromEnv().build())
    }

    DockerClient docker() {
        docker
    }

    boolean isAvailable() {
        try {
            docker.ping()
            return true
        } catch (DockerException e) {
            return false
        }
    }

    String daemon(String image) {
        def containerConfig = ContainerConfig.builder().image(image).build()
        ContainerCreation containerCreation
        try {
            containerCreation = docker.createContainer(containerConfig)
        } catch (ImageNotFoundException e) {
            docker.pull(image)
            containerCreation = docker.createContainer(containerConfig)
        }
        def id = containerCreation.id()
        docker.startContainer(id)
        startedContainers.add(id)
        registerShutdown(id)
        id
    }

    public void stop(String containerId) {
        docker.stopContainer(containerId, 30);
        startedContainers.remove(containerId);
    }

    public void stopAll() {
        for (String containerId : startedContainers) {
            stop(containerId);
        }
    }

    public void registerShutdown(String containerId) {
        getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Dockers.this.stop(containerId)
                } catch (DockerException e) {
                    LOG.info("Container {} is not running: {}", containerId, e.getMessage());
                }
            }
        });
    }

    public static void main(String[] args) {
        Dockers.dockers().daemon('mongo')
    }

}
