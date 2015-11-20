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

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;

import java.util.List;

import static org.apache.camel.component.spark.SparkConstants.SPARK_RDD_CALLBACK_HEADER;
import static org.apache.camel.component.spark.SparkConstants.SPARK_TRANSFORMATION_HEADER;
import static org.apache.camel.component.spark.SparkConstants.SPARK_RDD_HEADER;

public class SparkProducer extends DefaultProducer {

    public SparkProducer(SparkEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        JavaRDD rdd = resolveRdd(exchange);
        RddCallback rddCallback = resolveRddCallback(exchange);

        if(rddCallback == null) {
            Object body = exchange.getIn().getBody();
            Object transformation = exchange.getIn().getHeader(SPARK_TRANSFORMATION_HEADER);

            if(body instanceof Function) {
                if(transformation == null || transformation == SparkTransformation.FILTER) {
                    JavaRDD result = rdd.filter((Function) body);
                    collectResults(exchange, result);
                } else {
                    JavaRDD result = rdd.map((Function) body);
                    collectResults(exchange, result);
                }
            } else if(body instanceof FlatMapFunction) {
                JavaRDD result = rdd.flatMap((FlatMapFunction) body);
                collectResults(exchange, result);
            } else {
                throw new IllegalArgumentException("Unrecognized body type: " + body);
            }
        } else {
            Object body = exchange.getIn().getBody();
            Object result = body instanceof List ? rddCallback.onRdd(rdd, ((List)body).toArray(new Object[0])) : rddCallback.onRdd(rdd, body);
            collectResults(exchange, result);
        }
    }

    @Override
    public SparkEndpoint getEndpoint() {
        return (SparkEndpoint) super.getEndpoint();
    }

    // Helpers

    protected void collectResults(Exchange exchange, Object result) {
        if(result instanceof JavaRDD) {
            JavaRDD rddResults = (JavaRDD) result;
            if(getEndpoint().isCollect()) {
                exchange.getIn().setBody(rddResults.collect());
            } else {
                exchange.getIn().setBody(result);
                exchange.getIn().setHeader(SPARK_RDD_HEADER, result);
            }
        } else {
            exchange.getIn().setBody(result);
        }
    }

    protected JavaRDD resolveRdd(Exchange exchange) {
        if(exchange.getIn().getHeader(SPARK_RDD_HEADER) != null) {
            return (JavaRDD) exchange.getIn().getHeader(SPARK_RDD_HEADER);
        } else if(getEndpoint().getRdd() != null) {
            return getEndpoint().getRdd();
        } else {
            throw new IllegalStateException("No RDD defined.");
        }
    }

    protected RddCallback resolveRddCallback(Exchange exchange) {
        if(exchange.getIn().getHeader(SPARK_RDD_CALLBACK_HEADER) != null) {
            return  (RddCallback) exchange.getIn().getHeader(SPARK_RDD_CALLBACK_HEADER);
        } else {
            return getEndpoint().getRddCallback();
        }
    }

}