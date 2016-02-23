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
package io.rhiot.cloudplatform.service.camera;

import com.google.common.collect.ImmutableMap;
import io.rhiot.cloudplatform.connector.IoTConnector;
import org.apache.camel.builder.RouteBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.rhiot.cloudplatform.connector.Header.arguments;

public class CameraImageRotation extends RouteBuilder {

    private final IoTConnector connector;

    private final long storageQuota;

    private final int initialDelay;

    public CameraImageRotation(IoTConnector connector, long storageQuota, int initialDelay) {
        this.connector = connector;
        this.storageQuota = storageQuota;
        this.initialDelay = initialDelay;
    }

    @Override
    public void configure() throws Exception {
        from("timer:imageRotation?delay=" + initialDelay + "&period=60000").process(ex -> rotateImages());
    }

    private void rotateImages() {
        long imagesCount = connector.fromBus("document.count", long.class, arguments("CameraImage"));
        if (imagesCount * 10 > storageQuota * 1024) {
            Map<String, Object> query = ImmutableMap.of("query", ImmutableMap.of(), "page", 0, "size", 1000, "orderBy", Arrays.asList("id"));
            List<Map<String, Object>> imagesMetadata = connector.fromBus("document.findByQuery", query, List.class, arguments("CameraImage"));
            for(Map<String, Object> imageMetadata : imagesMetadata) {
                connector.toBusAndWait("binary.delete", imageMetadata.get("id"));
                connector.toBus("document.remove", arguments("CameraImage", imageMetadata.get("id")));
            }
        }
    }

}
