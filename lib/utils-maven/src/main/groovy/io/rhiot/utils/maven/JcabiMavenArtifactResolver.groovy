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
package io.rhiot.utils.maven

import com.jcabi.aether.Aether
import org.sonatype.aether.repository.RemoteRepository
import org.sonatype.aether.resolution.DependencyResolutionException
import org.sonatype.aether.util.artifact.DefaultArtifact
import org.sonatype.aether.util.artifact.JavaScopes

class JcabiMavenArtifactResolver extends ConfigurableMavenArtifactResolver {

    private final Aether aether

    public JcabiMavenArtifactResolver(List<Repository> repositories) {
        super(repositories);
        aether = initializeAether();
    }

    public JcabiMavenArtifactResolver() {
        super();
        aether = initializeAether();
    }

    private Aether initializeAether() {
        new Aether(repositories.collect { new RemoteRepository(it.id(), "default", it.url()) },
                new File(System.getProperty("user.home") + "/.m2/repository"));
    }

    @Override
    protected InputStream fetchArtifactStream(String groupId, String artifactId, String version, String extension) {
        try {
            def artifactWithDependencies = aether.resolve(
                    new DefaultArtifact(groupId, artifactId, "", extension, version),
                    JavaScopes.RUNTIME)
            def mainArtifacts = artifactWithDependencies.findAll { dependency ->
                dependency.artifactId == artifactId && dependency.groupId == groupId
            }
            if (mainArtifacts.size() > 1) {
                throw new RuntimeException("More than single main artifacts found: " + mainArtifacts);
            }
            return new FileInputStream(mainArtifacts.first().getFile());
        } catch (DependencyResolutionException e) {
            throw new MavenDependencyResolutionException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}