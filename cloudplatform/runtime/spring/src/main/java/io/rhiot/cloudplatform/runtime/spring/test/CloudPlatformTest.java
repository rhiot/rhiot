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
package io.rhiot.cloudplatform.runtime.spring.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rhiot.cloudplatform.runtime.spring.CloudPlatform;
import io.rhiot.cloudplatform.encoding.spi.PayloadEncoding;
import io.rhiot.cloudplatform.runtime.spring.IoTConnector;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

public abstract class CloudPlatformTest extends Assert {

    protected ObjectMapper json = new ObjectMapper();

    static private boolean dataStreamStarted;

    protected static CloudPlatform cloudPlatform = new CloudPlatform();

    protected static CamelContext camelContext;

    protected static ProducerTemplate producerTemplate;

    protected static PayloadEncoding payloadEncoding;

    protected static IoTConnector connector;


    @Before
    public void before() {
        System.setProperty("spring.activemq.broker.enabled", true + "");
        System.setProperty("spring.activemq.broker.amqpEnabled", true + "");
        int amqpPort = findAvailableTcpPort();
        System.setProperty("spring.activemq.broker.amqpPort", amqpPort + "");
        System.setProperty("AMQP_SERVICE_PORT", amqpPort + "");

        if(!dataStreamStarted) {
            beforeDataStreamStarted();
            cloudPlatform = cloudPlatform.start();
            camelContext = cloudPlatform.applicationContext().getBean(CamelContext.class);
            producerTemplate = camelContext.createProducerTemplate();
            payloadEncoding = cloudPlatform.applicationContext().getBean(PayloadEncoding.class);
            connector = cloudPlatform.applicationContext().getBean(IoTConnector.class);
            dataStreamStarted = true;
        }
        afterDataStreamStarted();
    }

    protected void beforeDataStreamStarted() {
    }

    protected void afterDataStreamStarted() {
    }

    @AfterClass
    public static void after() {
        try {
            cloudPlatform.stop();
        } finally {
            dataStreamStarted = false;
        }
    }

}
