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
package com.github.camellabs.iot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

import static java.lang.String.format;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;

public final class Mavens {

    private static final Properties VERSIONS = new Properties();

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
    public static File localMavenRepository() {
        return Paths.get(USER_HOME, ".m2", "repository").toFile();
    }

    public static String artifactVersionFromDependenciesProperties(String groupId, String artifactId) {
        return VERSIONS.getProperty(format("%s/%s/version", groupId, artifactId));
    }

    public static class MavenCoordinates {

        private final String groupId;

        private final String artifactId;

        private final String version;

        public MavenCoordinates(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }

        public static MavenCoordinates parseMavenCoordinates(String coordinates) {
            String[] parsedCoordinates = coordinates.split(":");
            return new MavenCoordinates(parsedCoordinates[0], parsedCoordinates[1], parsedCoordinates[2]);
        }

        public String groupId() {
            return groupId;
        }

        public String artifactId() {
            return artifactId;
        }

        public String version() {
            return version;
        }

    }

}