/**
 * Licensed to the Camel Labs under one or more
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
package com.github.camellabs.component.tinkerforge;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.camellabs.component.tinkerforge.ambientlight.AmbientlightEndpoint;
import com.github.camellabs.component.tinkerforge.distance.DistanceIREndpoint;
import com.github.camellabs.component.tinkerforge.dualrelay.DualRelayEndpoint;
import com.github.camellabs.component.tinkerforge.humidity.HumidityEndpoint;
import com.github.camellabs.component.tinkerforge.io.IO16Endpoint;
import com.github.camellabs.component.tinkerforge.io.IO4Endpoint;
import com.github.camellabs.component.tinkerforge.lcd20x4.Lcd20x4Endpoint;
import com.github.camellabs.component.tinkerforge.ledstrip.LedstripEndpoint;
import com.github.camellabs.component.tinkerforge.linearpoti.LinearPotentiometerEndpoint;
import com.github.camellabs.component.tinkerforge.motion.MotionEndpoint;
import com.github.camellabs.component.tinkerforge.piezospeaker.PiezoSpeakerEndpoint;
import com.github.camellabs.component.tinkerforge.rotarypoti.RotaryPotentiometerEndpoint;
import com.github.camellabs.component.tinkerforge.solidstaterelay.SolidStateRelayEndpoint;
import com.github.camellabs.component.tinkerforge.soundintensity.SoundIntensityEndpoint;
import com.github.camellabs.component.tinkerforge.temperature.TemperatureEndpoint;

public class TinkerforgeComponent extends DefaultComponent {
	private static final transient Logger LOG = LoggerFactory.getLogger(TinkerforgeComponent.class);

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
    	LOG.info("TinkerforgeComponent: creating endpoint. Uri: " + uri);
    	
    	TinkerforgeURI tinkerforgeUri = new TinkerforgeURI(uri);
    	
    	Endpoint endpoint = null;
    	
    	switch (tinkerforgeUri.getDeviceType()) {
            case ambientlight :     endpoint = new AmbientlightEndpoint(uri, this); break;
            case temperature :      endpoint = new TemperatureEndpoint(uri, this); break;
            case lcd20x4 :          endpoint = new Lcd20x4Endpoint(uri, this); break;
            case humidity :         endpoint = new HumidityEndpoint(uri, this); break;
            case io4 :              endpoint = new IO4Endpoint(uri, this); break;
            case io16 :             endpoint = new IO16Endpoint(uri, this); break;
            case distanceIR :       endpoint = new DistanceIREndpoint(uri, this); break;
            case ledstrip :         endpoint = new LedstripEndpoint(uri, this); break;
            case motion :           endpoint = new MotionEndpoint(uri, this); break;
            case soundintensity :   endpoint = new SoundIntensityEndpoint(uri, this); break;
            case piezospeaker :     endpoint = new PiezoSpeakerEndpoint(uri, this); break;
            case linearpoti :       endpoint = new LinearPotentiometerEndpoint(uri, this); break;
            case rotarypoti :       endpoint = new RotaryPotentiometerEndpoint(uri, this); break;
            case dualrelay :        endpoint = new DualRelayEndpoint(uri, this); break;
            case solidstaterelay :  endpoint = new SolidStateRelayEndpoint(uri, this); break;
        }
        
        setProperties(endpoint, tinkerforgeUri, parameters);

        return endpoint;
    }
    
    private void setProperties(Endpoint endpoint, TinkerforgeURI tinkerforgeUri, Map<String, Object> parameters) throws Exception {
        if (tinkerforgeUri.getUid() != null)        parameters.put("uid", tinkerforgeUri.getUid());
        if (tinkerforgeUri.getHost() != null)       parameters.put("host", tinkerforgeUri.getHost());
        if (tinkerforgeUri.getPort() != null)       parameters.put("port", tinkerforgeUri.getPort());
        //TODO: add credential properties to parameter map (The endpoints don't support this yet)
        setProperties(endpoint, parameters);
    }
}