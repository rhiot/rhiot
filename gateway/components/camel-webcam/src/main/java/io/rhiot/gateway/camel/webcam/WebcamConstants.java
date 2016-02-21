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

package io.rhiot.gateway.camel.webcam;

/**
 * Constants related to Webcam component.
 */
public final class WebcamConstants {

    // Constants

    public static final String WEBCAM_MOTION_EVENT_HEADER = "io.rhiot.webcam.webcamMotionEvent";

    public static final String V4L2_WEBCAM_LOADING_COMMAND = "modprobe bcm2835-v4l2";

    public static final String V4L2_SET_FORMAT_JPEG_COMMAND = "v4l2-ctl --set-fmt-video=pixelformat=3";

    public static final String V4L2_LIST_DEVICES_COMMAND = "v4l2-ctl --list-devices";

    public static final String WEBCAM_DEPENDENCIES_LINUX = "v4l-utils";

    public static final long   DEFAULT_WEBCAM_LOOKUP_TIMEOUT = 10000;

    // Constructors

    private WebcamConstants() {
    }

}
