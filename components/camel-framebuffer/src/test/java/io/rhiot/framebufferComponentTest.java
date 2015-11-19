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
package io.rhiot;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import io.rhiot.component.framebuffer.FramebufferComponent;
import io.rhiot.component.framebuffer.FramebufferConstants;

public class framebufferComponentTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint mock;

    @Produce(uri = "direct:start")
    protected ProducerTemplate sender;

    public static final String ROOT_4_TEST = "./target/testingfb";

    public static final String ROOT_4_TEST_SRC = "./src/test/testingfb";

    @Test
    public void testframebuffer() throws Exception {
        String uuid = UUID.randomUUID().toString();
        sender.sendBodyAndHeader(uuid, "foo", "bar");

        mock.expectedMinimumMessageCount(1);

        String uuidFb1 = Files.readAllLines(Paths.get(ROOT_4_TEST).resolve(FramebufferConstants.DEV).resolve("fb1"))
                .get(0);

        assertTrue(uuidFb1.startsWith(uuid));
        assertEquals(128, uuidFb1.length());
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {

        Path sourcePath = Paths.get(ROOT_4_TEST_SRC);
        Path targetPath = Paths.get(ROOT_4_TEST);

        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
                    throws IOException {
                Files.createDirectories(targetPath.resolve(sourcePath.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.copy(file, targetPath.resolve(sourcePath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });

        FramebufferComponent fc = new FramebufferComponent();
        fc.setRootDir(ROOT_4_TEST);

        this.context().addComponent("framebuffer", fc);

        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start").to("framebuffer://Test FB?createDevice=CREATE")
                        .to("framebuffer://fb1?createDevice=CREATE").to("mock:result");
            }
        };
    }
}
