package com.github.camellabs.component.tinkerforge;

import com.github.camellabs.component.tinkerforge.ambientlight.AmbientlightEndpoint;
import com.github.camellabs.component.tinkerforge.distance.DistanceEndpoint;
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
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TinkerforgeComponent extends DefaultComponent {
	private static final transient Logger LOG = LoggerFactory.getLogger(TinkerforgeComponent.class);

    protected Endpoint createEndpoint(String uri, String brickletType, Map<String, Object> parameters) throws Exception {
    	LOG.info("TinkerforgeComponent: creating endpoint. brickletType: "+brickletType+", Parameters: "+parameters);
    	
    	Endpoint endpoint;
    	
    	switch (brickletType) {
            case "ambientlight" :   endpoint = new AmbientlightEndpoint(uri, this); break;
            case "temperature" :    endpoint = new TemperatureEndpoint(uri, this); break;
            case "lcd20x4" :        endpoint = new Lcd20x4Endpoint(uri, this); break;
            case "humidity" :       endpoint = new HumidityEndpoint(uri, this); break;
            case "io4" :            endpoint = new IO4Endpoint(uri, this); break;
            case "io16" :           endpoint = new IO16Endpoint(uri, this); break;
            case "distance" :       endpoint = new DistanceEndpoint(uri, this); break;
            case "ledstrip" :       endpoint = new LedstripEndpoint(uri, this); break;
            case "motion" :         endpoint = new MotionEndpoint(uri, this); break;
            case "soundintensity" : endpoint = new SoundIntensityEndpoint(uri, this); break;
            case "piezospeaker" :   endpoint = new PiezoSpeakerEndpoint(uri, this); break;
            case "linearpoti" :     endpoint = new LinearPotentiometerEndpoint(uri, this); break;
            case "rotarypoti" :     endpoint = new RotaryPotentiometerEndpoint(uri, this); break;
            case "dualrelay" :      endpoint = new DualRelayEndpoint(uri, this); break;
            case "solidstaterelay" :endpoint = new SolidStateRelayEndpoint(uri, this); break;
            default:                throw new IllegalArgumentException("Unsupported BrickletType: "+brickletType);
        }
        
        setProperties(endpoint, parameters);

        return endpoint;
    }
}