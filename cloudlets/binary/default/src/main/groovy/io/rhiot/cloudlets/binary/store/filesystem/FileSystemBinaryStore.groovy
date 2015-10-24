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
package io.rhiot.cloudlets.binary.store.filesystem

import io.rhiot.cloudlets.binary.store.BinaryStore
import org.apache.commons.io.IOUtils

import static com.google.common.io.Files.createTempDir
import static io.rhiot.utils.Properties.stringProperty

class FileSystemBinaryStore implements BinaryStore {

    private final File storage

    private final IdToFileMappingStrategy idToFileMappingStrategy

    FileSystemBinaryStore(File storage, IdToFileMappingStrategy idToFileMappingStrategy) {
        this.storage = storage
        storage.mkdirs()

        this.idToFileMappingStrategy = idToFileMappingStrategy
    }

    FileSystemBinaryStore(File storage) {
        this(storage, new TenFirstLettersCustomIdToFileMappingStrategy())
    }

    FileSystemBinaryStore() {
        this(new File(stringProperty('binary_store_directory', createTempDir().absolutePath)))
    }

    @Override
    InputStream readData(String id) {
        new FileInputStream(new File(storage, idToFileMappingStrategy.mapIdToFile(id).getPath()))
    }

    @Override
    void storeData(String id, InputStream data) {
        def target = new File(storage, idToFileMappingStrategy.mapIdToFile(id).getPath())
        target.parentFile.mkdirs()
        IOUtils.copy(data, new FileOutputStream(target))
    }

}