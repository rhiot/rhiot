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

import static java.nio.file.StandardOpenOption.SYNC;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rhiot.component.framebuffer.convert.FramebufferConverter;

/**
 * The framebuffer producer.
 */
public class FramebufferProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(FramebufferProducer.class);
    private FramebufferEndpoint endpoint;
    private ByteBuffer doubleBuffer = null;
    private FramebufferConverter converter;

    public FramebufferProducer(FramebufferEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.doubleBuffer = ByteBuffer
                .allocate(endpoint.getHeight() * endpoint.getWitdh() * endpoint.getBitsPerPixel() / 8);
        this.converter = endpoint.getConverter();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        File fbFile = endpoint.getDev();
        Path path = Paths.get(fbFile.getAbsolutePath());
        doubleBuffer.clear();

        byte[] buffer = exchange.getIn().getBody(byte[].class);

        if (buffer == null) {

            buffer = Files.readAllBytes(path);
            exchange.getIn().setHeader(FramebufferConstants.CAMEL_FRAMEBUFFER_HEIGHT, endpoint.getHeight());
            exchange.getIn().setHeader(FramebufferConstants.CAMEL_FRAMEBUFFER_WITDH, endpoint.getWitdh());
            exchange.getIn().setHeader(FramebufferConstants.CAMEL_FRAMEBUFFER_BITS_PER_PIXEL,
                    endpoint.getBitsPerPixel());

            exchange.getIn().setBody(buffer);
        } else {
            if (buffer.length > doubleBuffer.limit()) {
                LOG.error("Truncate Body to Framebuffer : body size is too large");
            } else if (buffer.length < doubleBuffer.limit()) {
                LOG.info("Framebuffer.size is larger than body size");
            }

            if (converter != null) {
                endpoint.getConverter().converterByteToFramebuffer(buffer, doubleBuffer.array());
            } else {
                doubleBuffer.put(buffer, 0, Math.min(doubleBuffer.limit(), buffer.length));
            }
            if (endpoint.getCreateDevice() != null) {
                Files.write(path, doubleBuffer.array(), WRITE, SYNC, endpoint.getCreateDevice());
            } else {
                Files.write(path, doubleBuffer.array(), WRITE, SYNC);
            }
        }
    }
}
