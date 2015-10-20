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
package io.rhiot.component.tinkerforge;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import io.rhiot.component.tinkerforge.TinkerforgeDeviceType;
import io.rhiot.component.tinkerforge.TinkerforgeURI;
import org.junit.Test;

public class TinkerforgeURITest {

    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionOnIncorrectDeviceType() throws URISyntaxException {
        TinkerforgeURI uri = new TinkerforgeURI("tinkerforge:/non-existent/uid1");
    }
    
    @Test
    public void extractsDeviceTypeAndUid() throws URISyntaxException {
        TinkerforgeURI uri = new TinkerforgeURI("tinkerforge:/temperature/t1");
        assertEquals("DeviceType", TinkerforgeDeviceType.temperature, uri.getDeviceType());
        assertEquals("Uid", "t1", uri.getUid());
    }
    
    @Test
    public void extractsConnectionCredentials() throws URISyntaxException {
        TinkerforgeURI uri = new TinkerforgeURI("tinkerforge://username:password@host/temperature/t1");
        assertEquals("Username", "username", uri.getUsername());
        assertEquals("Password", "password", uri.getPassword());
    }
    
    @Test
    public void extractsHostAndPort() throws URISyntaxException {
        TinkerforgeURI uri = new TinkerforgeURI("tinkerforge://localhost:4223/temperature/t1");
        assertEquals("Host", "localhost", uri.getHost());
        assertEquals("Port", new Integer(4223), uri.getPort());
    }
    
    @Test
    public void extractsPathSections() throws URISyntaxException {
        TinkerforgeURI uri = new TinkerforgeURI("tinkerforge://localhost:4223/io16/t1/b/1");
        String[] sections = uri.getPathSections();
        assertEquals("Section 1",  "io16", sections[0]);
        assertEquals("Section 2",  "t1", sections[1]);
        assertEquals("Section 3",  "b", sections[2]);
        assertEquals("Section 4",  "1", sections[3]);
    }
}
