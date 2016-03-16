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
package io.rhiot.tooling.shell

import io.rhiot.cmd.DownloadManager
import io.rhiot.scanner.DeviceDetector
import io.rhiot.scanner.SimplePortScanningDeviceDetector
import io.rhiot.utils.maven.JcabiMavenArtifactResolver
import io.rhiot.utils.maven.MavenArtifactResolver
import io.rhiot.utils.process.DefaultProcessManager
import io.rhiot.utils.process.ProcessManager
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Shell {

    static void main(String... args) {
        new SpringApplication(Shell.class).run(args)
    }

    @ConditionalOnMissingBean
    @Bean
    ProcessManager processManager() {
        new DefaultProcessManager()
    }

    @Bean(destroyMethod = "close")
    MavenArtifactResolver mavenArtifactResolver() {
        new JcabiMavenArtifactResolver()
    }

    @Bean
    DownloadManager downloadManager() {
        new DownloadManager()
    }

    @Bean(destroyMethod = "close")
    DeviceDetector deviceDetector() {
        new SimplePortScanningDeviceDetector()
    }

}