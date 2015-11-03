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

package io.rhiot.utils;

import static java.util.Locale.ENGLISH;

/**
 * OS Utils, such as determining platform.
 */
public final class OsUtils {

    private static final String platform = System.getProperty("os.name").toLowerCase(ENGLISH);

    private OsUtils() {
    }

    /**
     * True if running on current platform.
     *
     * @param platform expected, eg Linux
     * @return <tt>true</tt> if running on given platform.
     */
    public static boolean isPlatform(String platform) {
        return getPlatform().contains(platform.toLowerCase(ENGLISH));
    }

    /**
     * Returns true if the OS is linux.
     * @return true if the OS is linux.
     */
    public static boolean isLinux(){
        return isPlatform("linux");
    }

    /**
     * Returns true if the OS is mac.
     * @return true if the OS is mac.
     */
    public static boolean isMac(){
        return isPlatform("mac");
    }

    /**
     * Returns current platform in lowercase English. 
     * @return <tt>os.name</tt> from the system properties in lowercase English.
     */
    public static String getPlatform(){
        return platform;
    }

}