/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.iot.deployer.maven;

import com.google.common.collect.ImmutableList;

import java.io.InputStream;
import java.util.List;

public abstract class ConfigurableMavenArtifactResolver implements MavenArtifactResolver {

    protected final List<Repository> repositories;

    public ConfigurableMavenArtifactResolver(List<Repository> repositories) {
        this.repositories = ImmutableList.copyOf(repositories);
    }

    public ConfigurableMavenArtifactResolver() {
        this.repositories = ImmutableList.of(Repository.mavenCentral());
    }

    @Override
    public InputStream artifactStream(String groupId, String artifactId, String version) {
        return artifactStream(groupId, artifactId, version, "jar");
    }

}
