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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class TinkerforgeURI {
	
	private String username;
	private String password;
	private String host = "localhost";
	private Integer port = 4223;
	private TinkerforgeDeviceType deviceType;
	private String uid;
	private String[] pathSections;
	
	public TinkerforgeURI(String uri) throws URISyntaxException {
	    URI uriObject = new URI(uri);
	    extractCredentials(uriObject);
	    extractHostAndPort(uriObject);
	    extractPathSections(uriObject);
	}

    private void extractHostAndPort(URI uriObject) {
        if (uriObject.getHost() != null)    host = uriObject.getHost();
        if (uriObject.getPort() != -1)      port = uriObject.getPort();
    }

    private void extractPathSections(URI uriObject) {
        try {
            String path = uriObject.getPath();
            if (path.startsWith("/")) path = path.substring(1);
            pathSections = path.split("/");
            deviceType = TinkerforgeDeviceType.valueOf(pathSections[0]);
            uid = pathSections[1];    
        } catch (Exception e) {
            throw new IllegalArgumentException("path should contain at least a valid deviceType and Uid: "+uriObject.getPath());
        }
    }

    private void extractCredentials(URI uriObject) {
        String userInfo = uriObject.getUserInfo();
        if (userInfo == null) return;
        String[] usernameAndPassword = userInfo.split(":");
	    if (usernameAndPassword.length == 2) {
	        username = usernameAndPassword[0];
	        password = usernameAndPassword[1];	        
	    } else {
	        throw new IllegalArgumentException("connection credentials should contain username and password");
	    }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public TinkerforgeDeviceType getDeviceType() {
        return deviceType;
    }

    public String getUid() {
        return uid;
    }

    public String[] getPathSections() {
        return pathSections;
    }

    @Override
    public String toString() {
        return "TinkerforgeURI [username=" + username + ", password="
                + password + ", host=" + host + ", port=" + port
                + ", deviceType=" + deviceType + ", uid=" + uid
                + ", pathSections=" + Arrays.toString(pathSections) + "]";
    }
    
}
