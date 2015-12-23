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
package io.rhiot.component.framebuffer.convert.impl;

import io.rhiot.component.framebuffer.convert.FramebufferConverter;

public class RGB888toRGB565Converter implements FramebufferConverter {

    @Override
    public void converterByteToFramebuffer(byte[] input, byte[] output) {
        int j = 0;
        byte r = 0;
        byte g = 0;
        byte b = 0;
        for (int i = 0; i < input.length && j < output.length; i = i + 3) {

            r = (byte) ((input[i] >> 3) & 0x001f); // useless with L36 we could
                                                   // use input[i] & 0xf8
            g = (byte) ((input[i + 1] >> 2) & 0x001f);
            b = (byte) ((input[i + 2] >> 3) & 0x001f);

            // http://www.theimagingsource.com/en_US/support/documentation/icimagingcontrol-class/PixelformatRGB565.htm
            output[j] = (byte) (g << 5 | b);
            output[j + 1] = (byte) (r << 3 | g >> 3);
            j = j + 2;
        }
    }

    @Override
    public void converterFramebufferToByte(byte[] input, byte[] output) {
        // TODO
    }
}
