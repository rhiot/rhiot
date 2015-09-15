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
package spring.boot;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static de.flapdoodle.embed.mongo.distribution.Version.V2_6_1;
import static de.flapdoodle.embed.process.runtime.Network.localhostIsIPv6;
import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@Configuration
public class EmbedMongoConfiguration {

    public static final int port = findAvailableTcpPort();

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MongodExecutable mongodExecutable() throws IOException {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(V2_6_1)
                .net(new Net(port, localhostIsIPv6()))
                .build();
        return MongodStarter.getDefaultInstance().prepare(mongodConfig);
    }

}
