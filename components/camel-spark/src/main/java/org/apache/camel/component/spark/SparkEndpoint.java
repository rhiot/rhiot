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

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Set;

@UriEndpoint(scheme = "spark", title = "Spark connector", producerOnly = true, label = "bigdata", syntax = "spark:label")
public class SparkEndpoint extends DefaultEndpoint {

    // Endpoint collaborators

    private JavaSparkContext sparkContext;

    private JavaRDD rdd;

    private RddCallback rddCallback;

    // Endpoint configuration

    private boolean collect = true;

    // Constructors

    public SparkEndpoint(String endpointUri, SparkComponent component) {
        super(endpointUri, component);
    }

    // Overridden

    @Override
    public Producer createProducer() throws Exception {
        return new SparkProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Spark component supports producer endpoints only.");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    // Resolvers

    protected JavaSparkContext resolveSparkContext() {
        if(sparkContext != null) {
            return sparkContext;
        } else if(getComponent().getJavaSparkContext() != null) {
            return getComponent().getJavaSparkContext();
        } else {
            Set<JavaSparkContext> contexts = getCamelContext().getRegistry().findByType(JavaSparkContext.class);
            if(contexts.isEmpty()) {
                return Sparks.createLocalSparkContext();
            } else if(contexts.size() == 1) {
                return contexts.iterator().next();
            } else {
                return null;
            }
        }
    }

    // Setters & getters


    @Override
    public SparkComponent getComponent() {
        return (SparkComponent) super.getComponent();
    }

    public JavaSparkContext getSparkContext() {
        return sparkContext;
    }

    public void setSparkContext(JavaSparkContext sparkContext) {
        this.sparkContext = sparkContext;
    }

    public JavaRDD getRdd() {
        return rdd;
    }

    public void setRdd(JavaRDD rdd) {
        this.rdd = rdd;
    }

    public RddCallback getRddCallback() {
        return rddCallback;
    }

    public void setRddCallback(RddCallback rddCallback) {
        this.rddCallback = rddCallback;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

}