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
package io.rhiot.cloudplatform.service.binding;

import com.google.common.truth.Truth;
import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest;
import org.junit.Test;

public class DestinationBindingTest extends CloudPlatformTest {

    @Test
    public void shouldReturnGreedyBinding() {
        DestinationBinding destinationBinding = cloudPlatform.applicationContext().getBean(DestinationBinding.class);
        String destination = destinationBinding.destination("foo");
        Truth.assertThat(destination).isEqualTo("amqp:foo.>");
    }

    @Test
    public void shouldReturnBindingWithPropertyArgument() {
        System.setProperty("arg2", "foo");
        DestinationBinding destinationBinding = cloudPlatform.applicationContext().getBean(DestinationBinding.class);
        String destination = destinationBinding.destination("service.operation.arg1.${arg2}");
        Truth.assertThat(destination).isEqualTo("service.operation.arg1.foo");
    }

    @Test
    public void shouldReturnBindingWithPropertyArguments() {
        System.setProperty("arg1", "foo");
        System.setProperty("arg2", "bar");
        DestinationBinding destinationBinding = cloudPlatform.applicationContext().getBean(DestinationBinding.class);
        String destination = destinationBinding.destination("service.operation.${arg1}.${arg2}");
        Truth.assertThat(destination).isEqualTo("service.operation.foo.bar");
    }

}