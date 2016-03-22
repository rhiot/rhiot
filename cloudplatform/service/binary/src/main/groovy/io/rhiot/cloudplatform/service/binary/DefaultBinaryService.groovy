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
package io.rhiot.cloudplatform.service.binary

import com.google.common.io.Files;
import io.rhiot.cloudplatform.service.binary.api.BinaryService
import org.slf4j.Logger

import static org.apache.commons.io.IOUtils.toByteArray;
import static org.slf4j.LoggerFactory.getLogger;

class DefaultBinaryService implements BinaryService {

    private static final Logger LOG = getLogger(DefaultBinaryService.class)

    private final File imagesDirectory

    DefaultBinaryService(File binariesDirectory) {
        this.imagesDirectory = binariesDirectory

        if(binariesDirectory.mkdirs()) {
            LOG.debug('Creating binaries store {} .', binariesDirectory)
        }
    }

    // Service API

    @Override
    void store(String identifier, byte[] data) {
        def target = binaryFile(identifier)
        LOG.debug('Writing binary data to {}.', target.absolutePath)
        Files.write(data, target)
    }

    @Override
    byte[] read(String identifier) {
        toByteArray(new FileInputStream(binaryFile(identifier)))
    }

    @Override
    void delete(String identifier) {
        binaryFile(identifier).delete()
    }

    // Helpers

    private File binaryFile(String identifier) {
        new File(imagesDirectory, identifier)
    }

}
