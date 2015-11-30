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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class URIRegexTest {

    @Test
    public void schemeGPIOTest() {
        Pattern p = Pattern.compile(Pi4jConstants.CAMEL_GPIO_URL_PATTERN);
        String url = "pi4j-gpio://1";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        Assert.assertEquals("1", m.group("gpioId"));
        Assert.assertEquals("pi4j-gpio", m.group("scheme"));
    }

    @Test
    public void schemeShortGPIOTest() {
        Pattern p = Pattern.compile(Pi4jConstants.CAMEL_GPIO_URL_PATTERN);
        String url = "1";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        Assert.assertEquals("1", m.group("gpioId"));
    }

    @Test
    public void schemeI2CTest() {
        Pattern p = Pattern.compile(Pi4jConstants.CAMEL_I2C_URL_PATTERN);
        String url = "pi4j-i2c://121/12";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        Assert.assertEquals("121", m.group("busId"));
        Assert.assertEquals("12", m.group("deviceId"));
        Assert.assertEquals("pi4j-i2c", m.group("scheme"));
    }

    @Test
    public void schemeI2CHexTest() {
        Pattern p = Pattern.compile(Pi4jConstants.CAMEL_I2C_URL_PATTERN);
        String url = "pi4j-i2c://121/0x12";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        Assert.assertEquals("121", m.group("busId"));
        Assert.assertEquals(18, Integer.decode(m.group("deviceId")).intValue());
        Assert.assertEquals("pi4j-i2c", m.group("scheme"));
    }

    @Test
    public void schemeShortI2CTest() {
        Pattern p = Pattern.compile(Pi4jConstants.CAMEL_I2C_URL_PATTERN);
        String url = "121/12";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        Assert.assertEquals("121", m.group("busId"));
        Assert.assertEquals("12", m.group("deviceId"));
    }

    @Test
    public void schemeShortI2CHexTest() {
        Pattern p = Pattern.compile(Pi4jConstants.CAMEL_I2C_URL_PATTERN);
        String url = "0x10/0x100";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        Assert.assertEquals(16, Integer.decode(m.group("busId")).intValue());
        Assert.assertEquals(256, Integer.decode(m.group("deviceId")).intValue());
    }

    @Test
    public void parseInt() {

        Assert.assertEquals(255, Integer.decode("0x000000ff").intValue());
        Assert.assertEquals(255, Integer.decode("0X000000ff").intValue());
        Assert.assertEquals(111, Integer.decode("111").intValue());
        Integer.decode("111");

    }

}
