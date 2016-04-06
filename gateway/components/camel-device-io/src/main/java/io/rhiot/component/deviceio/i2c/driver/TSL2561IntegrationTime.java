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

package io.rhiot.component.deviceio.i2c.driver;

/**
 * 
 */
public enum TSL2561IntegrationTime {

    INTEGRATIONTIME_13MS(0x00), INTEGRATIONTIME_101MS(0x01), INTEGRATIONTIME_402MS(0x02);

    final int integrationTime;

    TSL2561IntegrationTime(int integrationTime) {

        this.integrationTime = integrationTime;
    }

}
