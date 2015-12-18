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

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

import static io.rhiot.utils.maven.Repository.standardRepositories
import static java.util.concurrent.Executors.newSingleThreadExecutor

abstract class ConfigurableMavenArtifactResolver implements MavenArtifactResolver {

    protected final List<Repository> repositories

    protected final ExecutorService executor = newSingleThreadExecutor()

    ConfigurableMavenArtifactResolver(List<Repository> repositories) {
        this.repositories = repositories.collect().asImmutable()
    }

    ConfigurableMavenArtifactResolver() {
        this(standardRepositories())
    }

    abstract protected InputStream fetchArtifactStream(String groupId, String artifactId, String version, String extension)

    // Overridden

    @Override
    Future<InputStream> artifactStream(String groupId, String artifactId, String version, String extension) {
        executor.submit(new Callable() {
            @Override
            InputStream call() throws Exception {
                fetchArtifactStream(groupId, artifactId, version, extension)
            }
        })
    }

    @Override
    Future<InputStream> artifactStream(String groupId, String artifactId, String version) {
        return artifactStream(groupId, artifactId, version, 'jar');
    }

    @Override
    void close() {
        executor.shutdownNow()
    }

}
