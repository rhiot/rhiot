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
package io.rhiot.component.kura.cloud;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.eclipse.kura.cloud.CloudClient;
import org.eclipse.kura.cloud.CloudService;

@UriEndpoint(scheme = "kura-cloud", title = "Kura CLoud", label = "iot,kura,cloud", syntax = "kura-cloud:appId/appTopic")
public class KuraCloudEndpoint extends DefaultEndpoint {

    @UriParam(defaultValue = "")
    private String applicationId = "";

    @UriParam(defaultValue = "")
    private String topic = "";

    @UriParam(defaultValue = "")
    private int qos;

    @UriParam(defaultValue = "false")
    private boolean retain = false;

    @UriParam(defaultValue = "5")
    private int priority = 5;

    @UriParam(defaultValue = "false")
    private boolean control = false;

    @UriParam(defaultValue = "false")
    private boolean includeDeviceId = false;

    private CloudService cloudService = null;

    public KuraCloudEndpoint(String uri, KuraCloudComponent kuraCloudComponent, CloudService cloudService) {
        super(uri, kuraCloudComponent);
        this.cloudService = cloudService;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new IllegalArgumentException("Not yet developped");
    }

    @Override
    public Producer createProducer() throws Exception {
        CloudClient cloudClient = this.cloudService.newCloudClient(applicationId);
        return new KuraCloudProducer(this, cloudClient);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isControl() {
        return control;
    }

    public void setControl(boolean control) {
        this.control = control;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public boolean isIncludeDeviceId() {
        return includeDeviceId;
    }

    public void setIncludeDeviceId(boolean includeDeviceId) {
        this.includeDeviceId = includeDeviceId;
    }

}
