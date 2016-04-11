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
import com.spotify.docker.client.messages.HostConfig
import com.spotify.docker.client.messages.PortBinding
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static com.spotify.docker.client.DockerClient.ListContainersParam.allContainers

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

    String daemon(String image, String name, int ... ports) {
        def existingContainer = docker.listContainers(allContainers()).find{ it.names().contains('/' + name) }
        if(existingContainer == null) {
            def configBuilder = ContainerConfig.builder().image(image)
            if (ports.length > 0) {
                def portBindings = new HashMap<String, List<PortBinding>>()
                ports.each {
                    def hostPorts = new ArrayList<PortBinding>()
                    hostPorts.add(PortBinding.of("0.0.0.0", '' + it));
                    portBindings.put('' + it, hostPorts);
                }
                configBuilder.hostConfig(
                        HostConfig.builder().portBindings(portBindings).build()
                )
            }
            def containerConfig = configBuilder.build()
            ContainerCreation containerCreation
            try {
                containerCreation = name != null ? docker.createContainer(containerConfig, name) : docker.createContainer(containerConfig)
            } catch (ImageNotFoundException e) {
                docker.pull(image)
                containerCreation = docker.createContainer(containerConfig)
            }
            def id = containerCreation.id()
            docker.startContainer(id)
            registerShutdown(id)
            id
        } else if(existingContainer.status().startsWith('Up ')) {
            LOG.debug('Container named {} exists. Skipping creation. ', name)
            LOG.debug('Restarting container {}.', name)
            docker.restartContainer(existingContainer.id())
            registerShutdown(existingContainer.id())
            existingContainer.id()
        } else {
            LOG.debug('Container named {} exists. Skipping creation.', name)
        }
    }

    String daemon(String image) {
        daemon(image, null)
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
        startedContainers.add(containerId)
        Runtime.getRuntime().addShutdownHook(new Thread() {
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
        Dockers.dockers().daemon('mongo', 'poolxx', 27017)
        Thread.sleep(10 * 1000)
    }

}
