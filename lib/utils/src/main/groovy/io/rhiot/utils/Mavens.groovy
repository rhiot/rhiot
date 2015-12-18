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
package io.rhiot.utils

import groovy.transform.Immutable
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths

import static java.lang.String.format
import static org.apache.commons.lang3.SystemUtils.USER_HOME

public final class Mavens {

    private static final java.util.Properties VERSIONS = new java.util.Properties();

    private static final Logger LOG = LoggerFactory.getLogger(Mavens.class);

    private static final String DEPENDENCIES_PROPERTIES_PATH = "META-INF/maven/dependencies.properties";

    static {
        try {
            Enumeration<URL> dependenciesPropertiesStreams = Mavens.class.getClassLoader().getResources(DEPENDENCIES_PROPERTIES_PATH);
            if(!dependenciesPropertiesStreams.hasMoreElements()) {
                LOG.debug(format("No %s file found in the classpath.", DEPENDENCIES_PROPERTIES_PATH));
            }
            while (dependenciesPropertiesStreams.hasMoreElements()) {
                InputStream propertiesStream = dependenciesPropertiesStreams.nextElement().openStream();
                LOG.debug("Loading properties: " + propertiesStream);
                VERSIONS.load(propertiesStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Mavens() {
    }

    /**
     * Returns local Maven repository.
     * 
     * @return {@link java.io.File} pointing to the local Maven repository.
     */
    static File localMavenRepository() {
        return Paths.get(USER_HOME, ".m2", "repository").toFile();
    }

    static String artifactVersionFromDependenciesProperties(String groupId, String artifactId) {
        return VERSIONS.getProperty(format("%s/%s/version", groupId, artifactId));
    }

    @Immutable
    static class MavenCoordinates {

        public static final def DEFAULT_COORDINATES_SEPARATOR = ':'

        String groupId

        String artifactId

        String version

        static MavenCoordinates parseMavenCoordinates(String coordinates) {
            parseMavenCoordinates(coordinates, DEFAULT_COORDINATES_SEPARATOR)
        }

        static MavenCoordinates parseMavenCoordinates(String coordinates, String separator) {
            def parsedCoordinates = coordinates.split(separator)
            new MavenCoordinates(parsedCoordinates[0], parsedCoordinates[1], parsedCoordinates[2]);
        }

    }

}