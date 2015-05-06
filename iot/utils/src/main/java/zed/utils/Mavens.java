package zed.utils;

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

}