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
package io.rhiot.component.framebuffer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link FramebufferEndpoint}.
 */
public class FramebufferComponent extends UriEndpointComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FramebufferEndpoint.class);

    private String rootDir = FramebufferConstants.ROOT;

    public FramebufferComponent() {
        super(FramebufferEndpoint.class);
    }

    public FramebufferComponent(CamelContext context) {
        super(context, FramebufferEndpoint.class);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new FramebufferEndpoint(uri, this);

        Path dev = retrieveCorrectFB(remaining);

        String devFileName = dev.toFile().getName();

        Path sysClassGraphics = Paths.get(getRootDir()).resolve(FramebufferConstants.SYS_CLASS_GRAPHICS)
                .resolve(devFileName);

        // Extract size and definition from virtual fs
        Path bitsPerPixelFile = sysClassGraphics.resolve(FramebufferConstants.BITS_PER_PIXEL_FILE);
        Path sizeFile = sysClassGraphics.resolve(FramebufferConstants.VIRTUAL_SIZE);

        String bitsPerPixel = Files.readAllLines(bitsPerPixelFile).get(0);
        String size = Files.readAllLines(sizeFile).get(0);

        if (parameters.get(FramebufferConstants.BITS_PER_PIXEL) == null) {
            parameters.put(FramebufferConstants.BITS_PER_PIXEL, bitsPerPixel);
        }
        if (parameters.get(FramebufferConstants.HEIGHT) == null) {
            parameters.put(FramebufferConstants.HEIGHT, size.split(",")[0]);
        }
        if (parameters.get(FramebufferConstants.WITDH) == null) {
            parameters.put(FramebufferConstants.WITDH, size.split(",")[1]);
        }
        parameters.put(FramebufferConstants.DEV, dev.toFile());
        setProperties(endpoint, parameters);
        return endpoint;
    }

    public Path retrieveCorrectFB(String pathOrName) throws IOException {

        Path dir = FileSystems.getDefault().getPath(getRootDir()).resolve(FramebufferConstants.SYS_CLASS_GRAPHICS);
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir, FramebufferConstants.FB);
        // Find device by internal name
        for (Path path : stream) {
            LOG.debug("find " + path.getFileName());
            Path nameFile = path.resolve(FramebufferConstants.NAME);
            if (Files.isReadable(nameFile)) {
                String fbName = Files.readAllLines(nameFile).get(0);
                if (fbName.compareTo(pathOrName) == 0) {
                    stream.close();
                    return FileSystems.getDefault().getPath(getRootDir()).resolve(FramebufferConstants.DEV)
                            .resolve(path.getFileName());
                }
            }
        }
        stream.close();
        // Find device by name
        Path dev = dir.resolve(pathOrName);
        if (Files.isDirectory(dev)) {
            return Paths.get(getRootDir()).resolve(FramebufferConstants.DEV).resolve(pathOrName);
        }

        throw new IllegalArgumentException("Please check Framebuffer device name");
    }

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

}
