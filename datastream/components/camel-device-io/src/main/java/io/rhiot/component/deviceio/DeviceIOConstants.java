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
package io.rhiot.component.deviceio;

import jdk.dio.i2cbus.I2CDeviceConfig;

/**
 * Constants Class
 */
public final class DeviceIOConstants {

    public static final String CAMEL_DEVICE_IO = "CamelDio";
    public static final String CAMEL_DEVICE_IO_PIN = CAMEL_DEVICE_IO + ".pin";
    public static final String CAMEL_DEVICE_IO_STATE = CAMEL_DEVICE_IO + ".pinState";
    public static final String CAMEL_DEVICE_IO_TYPE = CAMEL_DEVICE_IO + ".pinType";
    public static final String CAMEL_DEVICE_IO_VALUE = CAMEL_DEVICE_IO + ".pinValue";
    public static final String CAMEL_DEVICE_IO_ACTION = CAMEL_DEVICE_IO + ".pinAction";
    public static final String CAMEL_DEVICE_IO_NAME = CAMEL_DEVICE_IO + ".pinName";
    public static final String CAMEL_DEVICE_IO_TIMESTAMP = CAMEL_DEVICE_IO + ".timeStamp";

    public static final String CAMEL_DEVICE_IO_THREADPOOL = "device-io-gpio-camel-";

    public static final String CAMEL_GPIO_URL_PATTERN = "((?<scheme>deviceio-gpio)://?)?(?<gpioId>[a-zA-Z0-9_-]+)";
    public static final String CAMEL_I2C_URL_PATTERN = "((?<scheme>deviceio-i2c)://)?(?<busId>(0x)?[0-9a-f]+)/(?<deviceId>(0x)?[0-9a-f]+)";
    public static final String CAMEL_GPIO_ID = "gpioId";
    public static final String CAMEL_URL_TYPE = "type";
    public static final String CAMEL_SPLIT_REGEX = "[A-Z_]+(\\|[A-Z_]+)*";
    public static final String CAMEL_SPLIT = "\\|";

    public static final String CAMEL_I2C_DEVICE_ID = "deviceId";
    public static final String CAMEL_I2C_BUS_ID = "busId";
    public static final int CAMEL_I2C_DEFAULT_BUS_ID = 1;

    public static final String CAMEL_I2C_DRIVER_LOCATION = "/META-INF/services/io/rhiot/component/deviceio/i2c/";
    public static final boolean CAMEL_I2C_DIO_LENIENT = true;

    public static final int CAMEL_I2C_DIO_SERIAL_CLOCK = 3400000; // 3.4MHz
    public static final int CAMEL_I2C_DIO_ADDRESS_SIZE_BITS = I2CDeviceConfig.ADDR_SIZE_7;
    public static final int CAMEL_I2C_DIO_SUBADDRESS_SIZE_BITS = 1;
    public static final int CAMEL_I2C_DIO_BYTE_SHIFT = 8;
    public static final int CAMEL_I2C_DIO_WORD_SHIFT = 16;

    private DeviceIOConstants() {
        // Constants class
    }
}
