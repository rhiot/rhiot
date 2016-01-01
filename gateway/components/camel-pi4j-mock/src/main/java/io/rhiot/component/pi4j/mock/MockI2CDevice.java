/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rhiot.component.pi4j.mock;

import com.pi4j.io.i2c.I2CDevice;

import java.io.IOException;

/**
 * 
 */
public class MockI2CDevice implements I2CDevice {

    public void write(byte b) throws IOException {
    }

    public void write(byte[] buffer, int offset, int size) throws IOException {
    }

    public void write(int address, byte b) throws IOException {
    }

    public void write(int address, byte[] buffer, int offset, int size) throws IOException {
    }

    public int read() throws IOException {
        return 121;
    }

    public int read(byte[] buffer, int offset, int size) throws IOException {
        for (int i = offset; i < offset + size; i++) {
            buffer[i] = (byte) (i * 3 + 1);
        }
        return size;
    }

    public int read(int address) throws IOException {
        return 12;
    }

    public int read(int address, byte[] buffer, int offset, int size) throws IOException {
        return 0;
    }

    public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset, int readSize)
            throws IOException {
        return 0;
    }

}
