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
package org.apache.camel.component.spark;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Map;

public class SparkComponent extends UriEndpointComponent {

    private JavaSparkContext javaSparkContext;

    public SparkComponent() {
        super(SparkEndpoint.class);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        return new SparkEndpoint(uri, this);
    }

    public JavaSparkContext getJavaSparkContext() {
        return javaSparkContext;
    }

    public void setJavaSparkContext(JavaSparkContext javaSparkContext) {
        this.javaSparkContext = javaSparkContext;
    }

}
