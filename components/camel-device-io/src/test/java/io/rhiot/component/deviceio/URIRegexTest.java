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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class URIRegexTest {

    @Test
    public void schemeGPIOTest() {
        Pattern p = Pattern.compile(DeviceIOConstants.CAMEL_GPIO_URL_PATTERN);
        String url = "deviceio-gpio://1";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        Assert.assertEquals("1", m.group("gpioId"));
        Assert.assertEquals("deviceio-gpio", m.group("scheme"));
    }

    @Test
    public void schemeShortGPIOTest() {
        Pattern p = Pattern.compile(DeviceIOConstants.CAMEL_GPIO_URL_PATTERN);
        String url = "1";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        Assert.assertEquals("1", m.group("gpioId"));
    }

    @Test
    public void optionSplitTest() {
        Pattern p = Pattern.compile(DeviceIOConstants.CAMEL_SPLIT_REGEX);
        String url = "ABC_DE";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        String[] tot = {"ABC_DE"};
        Assert.assertArrayEquals(tot, url.split(DeviceIOConstants.CAMEL_SPLIT));
    }

    @Test
    public void optionSplitTest2() {
        Pattern p = Pattern.compile(DeviceIOConstants.CAMEL_SPLIT_REGEX);
        String url = "ABC_DE|EF_GHI";
        Matcher m = p.matcher(url);
        Assert.assertTrue(m.matches());
        String[] tot = {"ABC_DE", "EF_GHI"};
        Assert.assertArrayEquals(tot, url.split(DeviceIOConstants.CAMEL_SPLIT));
    }

    @Test
    public void optionSplitTest3() {
        Pattern p = Pattern.compile(DeviceIOConstants.CAMEL_SPLIT_REGEX);
        String url = "ABC_DE|E3F_GHI";
        Matcher m = p.matcher(url);
        Assert.assertFalse(m.matches());
    }

    @Test
    public void optionSplitTest4() {
        Pattern p = Pattern.compile(DeviceIOConstants.CAMEL_SPLIT_REGEX);
        String url = "ABC_DE|EaF_GHI";
        Matcher m = p.matcher(url);
        Assert.assertFalse(m.matches());
    }
}
