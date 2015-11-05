/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rhiot.component.webcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.dummy.WebcamDummyDevice;
import io.rhiot.utils.OsUtils;
import io.rhiot.utils.install.DefaultInstaller;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assume.assumeTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class WebcamComponentTest extends CamelTestSupport {

    private static Webcam webcam = mock(Webcam.class);
    private static final Map<String, Webcam> webcams = new HashMap<>();

    @BeforeClass
    public static void before() throws IOException {
        BufferedImage image = ImageIO.read(WebcamComponentTest.class.getResourceAsStream("rhiot.png"));
        given(webcam.getImage()).willReturn(image);
        given(webcam.open()).willReturn(true);
        given(webcam.getDevice()).willReturn(new WebcamDummyDevice(1));
        webcams.put("dummy", webcam);
         
        //Avoid the driver error when webcam/bridj loads the native lib
        if (OsUtils.isLinux()) {
            assumeTrue(new DefaultInstaller().isInstalled(WebcamConstants.WEBCAM_DEPENDENCIES_LINUX));
        }
    }
    
    @Test 
    public void testWebcamNames() throws Exception {
        
        //If we can find a webcam, we must have webcam names too
        Webcam webcam = null;
        try {
            webcam = Webcam.getDefault();
        } catch (Error e) {
            //cannot run this test here, eg build
        }

        Assume.assumeNotNull(webcam);
        webcam.close();
        
        WebcamComponent component = new WebcamComponent(context);
        HashMap<String, Webcam> webcams = new HashMap<>();
        webcams.put(webcam.getDevice().getName(), webcam);
        component.setWebcams(webcams);
        component.doStart();
        
        assertFalse(component.getWebcamNames().isEmpty());
        assertEquals(webcam, component.getWebcam(webcam.getDevice().getName(), null));
        component.stop();
    }
    
    @Test
    public void testWebcamFindByName() throws Exception {
        
        //If we can find a webcam, we must have webcam names too
        Webcam webcam = null;
        try {
            webcam = Webcam.getDefault();
        } catch (Error e) {
            //cannot run this test here, eg build
        }

        Assume.assumeNotNull(webcam);
        
        WebcamComponent component = new WebcamComponent(context);
        HashMap<String, Webcam> webcams = new HashMap<>();
        webcams.put(webcam.getName(), webcam);
        component.setWebcams(webcams);
        component.doStart();
        
        assertEquals(webcam, component.getWebcam(webcam.getName(), null));
        component.stop();
    }
    
    @Test(expected = RuntimeException.class)
    public void testDriverInstance() throws Exception {
        
        WebcamComponent component = new WebcamComponent(context);
        component.setDriver("invalid.driver");
        component.start();
    }
    
    @Test 
    public void testDriver() throws Exception {
        
        WebcamComponent component = new WebcamComponent(context);
        component.setDriver(CustomDriver.class.getName());
        component.start();

        assertTrue(component.isStarted());
        component.stop();
    }

    @Test
    public void smokeTest() throws Exception {
        Thread.sleep(2000);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("webcam", webcam);
        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("webcam:cam").to("seda:mock");
            }
        };
    }

}
