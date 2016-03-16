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
package io.rhiot.cmd

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger

import java.nio.file.Paths

import static org.slf4j.LoggerFactory.getLogger

class DownloadManager {

    private final static Logger LOG = getLogger(DownloadManager.class)

    private final File downloadDirectory = Paths.get(SystemUtils.userHome.absolutePath, '.rhiot', 'downloads').toFile()

    def download(URL source, String name) {
        def imageZip = downloadedFile(name)
        if(!imageZip.exists()) {
            LOG.debug('File {} does not exist - downloading...', imageZip.absolutePath)
            imageZip.parentFile.mkdirs()
            IOUtils.copyLarge(source.openStream(), new FileOutputStream(imageZip))
            LOG.debug('Saved downloaded file to {}.', imageZip.absolutePath)
        } else {
            LOG.debug('File {} exists - download skipped.', imageZip)
        }
    }

    File downloadedFile(String name) {
        new File(downloadDirectory, name)
    }

    File downloadDirectory() {
        downloadDirectory
    }

}
