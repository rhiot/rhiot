/*
 * *
 *  * Licensed to the Rhiot under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  
 *  
 */

package com.github.camellabs.iot.cloudlet.webcam.domain;

import java.util.Date;

public class WebcamImage {

    private final String id;

    private final Date timestamp;

    private final String imageFormat;
    
    private final String filepath;
    
    private final String webcamId;

    public WebcamImage(String id, Date timestamp, String imageFormat, String filepath, String webcamId) {
        this.id = id;
        this.timestamp = timestamp;
        this.imageFormat = imageFormat;
        this.filepath = filepath;
        this.webcamId = webcamId;
    }

    public String getId() {
        return id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getWebcamId() {
        return webcamId;
    }
}
