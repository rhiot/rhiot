/**
 * Licensed to the Eclipse Foundation under one or more
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
package io.rhiot.cloudplatform.service.device.api;

import java.util.Map;

public class LinkObject {

    private String url;

    private Map<String, Object> attributes;

    private Integer objectId;

    private Integer objectInstanceId;

    private Integer resourceId;

    public LinkObject(String url, Map<String, Object> attributes, Integer objectId, Integer objectInstanceId, Integer resourceId) {
        this.url = url;
        this.attributes = attributes;
        this.objectId = objectId;
        this.objectInstanceId = objectInstanceId;
        this.resourceId = resourceId;
    }

    public LinkObject() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getObjectInstanceId() {
        return objectInstanceId;
    }

    public void setObjectInstanceId(Integer objectInstanceId) {
        this.objectInstanceId = objectInstanceId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

}