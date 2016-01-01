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

import com.pi4j.io.spi.SpiDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class SpiDeviceMock implements SpiDevice {

    @Override
    public String write(String s, Charset charset) throws IOException {
        return null;
    }

    @Override
    public String write(String s, String s1) throws IOException {
        return null;
    }

    @Override
    public ByteBuffer write(ByteBuffer byteBuffer) throws IOException {
        return null;
    }

    @Override
    public byte[] write(InputStream inputStream) throws IOException {
        return new byte[0];
    }

    @Override
    public int write(InputStream inputStream, OutputStream outputStream) throws IOException {
        return 0;
    }

    @Override
    public byte[] write(byte[] bytes, int i, int i1) throws IOException {
        return new byte[0];
    }

    @Override
    public byte[] write(byte... bytes) throws IOException {
        return new byte[0];
    }

    @Override
    public short[] write(short[] shorts, int i, int i1) throws IOException {
        return new short[0];
    }

    @Override
    public short[] write(short... shorts) throws IOException {
        return new short[0];
    }
}
