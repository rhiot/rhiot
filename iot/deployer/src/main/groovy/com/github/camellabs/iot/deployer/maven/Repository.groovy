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
package com.github.camellabs.iot.deployer.maven;

class Repository {

    private final String id

    private final String url

    public Repository(String id, String url) {
        this.id = id;
        this.url = url;
    }

    static Repository mavenCentral() {
        new Repository("mavenCentral", "https://repo1.maven.org/maven2");
    }

    static Repository sonatypeSnapshots() {
        new Repository('sonatypeSnapshots', "https://oss.sonatype.org/content/repositories/snapshots");
    }

    static Repository apacheSnapshots() {
        new Repository('apacheSnapshots', 'https://repository.apache.org/content/repositories/snapshots')
    }

    static List<Repository> standardRepositories() {
        [mavenCentral(), sonatypeSnapshots(), apacheSnapshots()].asImmutable()
    }

    public String id() {
        return id;
    }

    public String url() {
        return url;
    }

}
