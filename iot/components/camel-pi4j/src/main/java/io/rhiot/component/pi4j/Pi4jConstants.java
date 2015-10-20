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
package io.rhiot.component.pi4j;

/**
 * Constants Class
 */
public final class Pi4jConstants {
    public static final String LOG_COMPONENT = "log:iot.rhiot.component.pi4j?showAll=true&multiline=true";
    public static final String CAMEL_I2C_DRIVER_LOCATION = "/META-INF/services/io/rhiot/component/pi4j/i2c/";
    public static final String CAMEL_ID_ROUTE = "raspberry-pi";
    public static final String PROVIDER_NAME = "RaspberryPi GPIO Provider Mock";
    public static final String CAMEL_RBPI = "CamelPi4j";
    public static final String CAMEL_RBPI_PIN = CAMEL_RBPI + ".pin";
    public static final String CAMEL_RBPI_PIN_STATE = CAMEL_RBPI + ".pinState";
    public static final String CAMEL_RBPI_PIN_TYPE = CAMEL_RBPI + ".pinType";
    public static final String CAMEL_RBPI_PIN_VALUE = CAMEL_RBPI + ".pinValue";
    public static final String CAMEL_RBPI_URL_PATTERN = "((?<scheme>raspberrypi):(//)?)?(?<type>gpio|i2c|serial|spi)/(?<id>[a-zA-Z0-9_-]+)(/(?<device>[a-zA-Z0-9_-]+))?";
    public static final String CAMEL_GPIO_URL_PATTERN = "((?<scheme>pi4j-gpio)://)?(?<gpioId>[0-9A-Z_]+)";
    public static final String CAMEL_I2C_URL_PATTERN = "((?<scheme>pi4j-i2c)://)?(?<busId>(0x)?[0-9a-f]+)/(?<deviceId>(0x)?[0-9a-f]+)";
    public static final String CAMEL_URL_ID = "id";
    public static final String CAMEL_URL_TYPE = "type";
    public static final String CAMEL_URL_DEVICE = "device";
    public static final String CAMEL_DEVICE_ID = "deviceId";
    public static final String CAMEL_BUS_ID = "busId";
    public static final String CAMEL_GPIO_ID = "gpioId";
    public static final String CAMEL_GPIO_CLAZZ = "com.pi4j.io.gpio.RaspiPin";
    public static final boolean CAMEL_PI4j_LENIENT = true;

    private Pi4jConstants() {
        // Constants class
    }
}
