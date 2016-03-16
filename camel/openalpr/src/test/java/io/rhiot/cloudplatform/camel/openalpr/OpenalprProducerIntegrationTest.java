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
package io.rhiot.cloudplatform.camel.openalpr;

import com.google.common.truth.Truth;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static io.rhiot.utils.process.Processes.canExecuteCommand;
import static org.junit.Assume.assumeTrue;

public class OpenalprProducerIntegrationTest extends CamelTestSupport {

    @BeforeClass
    public static void beforeClass() {
        assumeTrue(canExecuteCommand("docker", "version"));
    }

    @Test
    public void shouldFindPlate() {
        InputStream image = getClass().getResourceAsStream("/h786poj.jpg");
        List<PlateMatch> plateMatches = template.requestBody("openalpr:plateReader", image , List.class);
        Truth.assertThat(plateMatches.get(0).getPlateNumber()).isEqualTo("H786P0J");
    }

}
