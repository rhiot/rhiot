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
package io.rhiot.component.framebuffer;

public final class FramebufferConstants {

    public static final String NAME = "name";
    public static final String ROOT = "/";
    public static final String SYS_CLASS_GRAPHICS = "sys/class/graphics";
    public static final String WITDH = "witdh";
    public static final String BITS_PER_PIXEL_FILE = "bits_per_pixel";
    public static final String DEV = "dev";
    public static final String FB = "fb*";
    public static final String HEIGHT = "height";
    public static final String BITS_PER_PIXEL = "bitsPerPixel";
    public static final String VIRTUAL_SIZE = "virtual_size";
    public static final String CAMEL_FRAMEBUFFER = "CamelFramebuffer";
    public static final String CAMEL_FRAMEBUFFER_BITS_PER_PIXEL = CAMEL_FRAMEBUFFER + ".bits_per_pixel";
    public static final String CAMEL_FRAMEBUFFER_HEIGHT = CAMEL_FRAMEBUFFER + ".height";
    public static final String CAMEL_FRAMEBUFFER_WITDH = CAMEL_FRAMEBUFFER + ".witdh";

    private FramebufferConstants() {
    }

}
