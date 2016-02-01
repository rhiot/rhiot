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
package io.rhiot.cloudplatform.service.binary;

import io.rhiot.cloudplatform.service.binary.api.BinaryService;

import java.io.*;

import static org.apache.commons.io.IOUtils.toByteArray;
import static org.apache.commons.io.IOUtils.write;

public class DefaultBinaryService implements BinaryService {

    private final File imagesDirectory;

    public DefaultBinaryService(File imagesDirectory) {
        this.imagesDirectory = imagesDirectory;

        imagesDirectory.mkdirs();
    }

    @Override
    public void store(String identifier, byte[] data) {
        try {
            write(data, new FileOutputStream(new File(imagesDirectory, identifier)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] read(String identifier) {
        try {
            return toByteArray(new FileInputStream(new File(imagesDirectory, identifier)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
