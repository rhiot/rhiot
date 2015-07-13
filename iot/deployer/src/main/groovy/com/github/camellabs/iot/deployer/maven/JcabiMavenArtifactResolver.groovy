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
package com.github.camellabs.iot.deployer.maven

import com.google.common.base.Function
import com.jcabi.aether.Aether
import org.sonatype.aether.artifact.Artifact
import org.sonatype.aether.repository.RemoteRepository
import org.sonatype.aether.resolution.DependencyResolutionException
import org.sonatype.aether.util.artifact.DefaultArtifact
import org.sonatype.aether.util.artifact.JavaScopes

import static com.google.common.collect.Lists.transform

class JcabiMavenArtifactResolver extends ConfigurableMavenArtifactResolver {

    private final Aether aether;

    public JcabiMavenArtifactResolver(List<Repository> repositories) {
        super(repositories);
        aether = initializeAether();
    }

    public JcabiMavenArtifactResolver() {
        super();
        aether = initializeAether();
    }

    private Aether initializeAether() {
        return new Aether(transform(repositories, new Function<Repository, RemoteRepository>() {
            @Override
            public RemoteRepository apply(Repository repository) {
                return new RemoteRepository(repository.id(), "default", repository.url());
            }
        }), new File(System.getProperty("user.home") + "/.m2/repository"));
    }

    @Override
    public InputStream artifactStream(String groupId, String artifactId, String version, String extension) {
        try {
            List<Artifact> artifactWithDependencies = aether.resolve(
                    new DefaultArtifact(groupId, artifactId, "", extension, version),
                    JavaScopes.RUNTIME);
            List<Artifact> mainArtifacts = artifactWithDependencies.findAll { dependency ->
                dependency.getArtifactId().equals(artifactId) &&
                        dependency.getGroupId().equals(groupId) &&
                        dependency.getVersion().equals(version)
            }
            if (mainArtifacts.size() > 1) {
                throw new RuntimeException("More than single main artifacts found: " + mainArtifacts);
            }
            return new FileInputStream(mainArtifacts.get(0).getFile());
        } catch (DependencyResolutionException e) {
            throw new MavenDependencyResolutionException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}