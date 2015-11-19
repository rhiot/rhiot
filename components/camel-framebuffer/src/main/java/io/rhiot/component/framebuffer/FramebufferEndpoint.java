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

import io.rhiot.component.framebuffer.convert.FramebufferConverter;

import java.io.File;
import java.nio.file.StandardOpenOption;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a framebuffer endpoint.
 */
@UriEndpoint(scheme = "framebuffer", syntax = "framebuffer://dev", label = "framebuffer", title = "framebuffer")
public class FramebufferEndpoint extends DefaultEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(FramebufferEndpoint.class);

    @UriPath
    @Metadata(required = "true")
    private File dev;
    @UriParam(defaultValue = "0", description = "Auto calculated")
    private int height;
    @UriParam(defaultValue = "0", description = "Auto calculated")
    private int witdh;
    @UriParam(defaultValue = "0", description = "Auto calculated")
    private int bitsPerPixel;
    @UriParam()
    private FramebufferConverter converter;
    @UriParam(description = "For test only")
    private StandardOpenOption createDevice;

    public FramebufferEndpoint() {
    }

    public FramebufferEndpoint(String uri, FramebufferComponent component) {
        super(uri, component);
    }

    public FramebufferEndpoint(String endpointUri) {
        super(endpointUri);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new FramebufferProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public File getDev() {
        return dev;
    }

    public void setDev(File dev) {
        this.dev = dev;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWitdh() {
        return witdh;
    }

    public void setWitdh(int witdh) {
        this.witdh = witdh;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public void setBitsPerPixel(int bitPerPixel) {
        this.bitsPerPixel = bitPerPixel;
    }

    public FramebufferConverter getConverter() {
        return converter;
    }

    public void setConverter(FramebufferConverter converter) {
        this.converter = converter;
    }

    public StandardOpenOption getCreateDevice() {
        return createDevice;
    }

    public void setCreateDevice(StandardOpenOption createDevice) {
        this.createDevice = createDevice;
    }

}
