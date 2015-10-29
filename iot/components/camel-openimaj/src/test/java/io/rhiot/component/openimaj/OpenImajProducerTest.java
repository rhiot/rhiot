
package io.rhiot.component.openimaj;

import org.apache.camel.TypeConverter;
import org.apache.camel.impl.DefaultClassResolver;
import org.apache.camel.impl.DefaultFactoryFinderResolver;
import org.apache.camel.impl.DefaultPackageScanClassResolver;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.converter.DefaultTypeConverter;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.ReflectionInjector;
import org.apache.camel.util.ServiceHelper;
import org.junit.*;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.FacialKeypoint;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;

import java.util.List;

public class OpenImajProducerTest extends CamelTestSupport {

    private TypeConverter converter = new DefaultTypeConverter(new DefaultPackageScanClassResolver(),
            new ReflectionInjector(), new DefaultFactoryFinderResolver().resolveDefaultFactoryFinder(new DefaultClassResolver()));


    @Before
    public void setUp() throws Exception {
        super.setUp();
        ServiceHelper.startService(converter);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        FKEFaceDetector faceDetector = new FKEFaceDetector();
        registry.bind("fkeFaceDetector", faceDetector);
        
        return registry;
    }
    
    @Test
    public void testFaceDetector() throws Exception {
        DetectedFace face = template.requestBody("openimaj:face-detection", OpenImajProducerTest.class.getResourceAsStream("test-face.png"), DetectedFace.class);
        assertNotNull(face);
        assertTrue(face.getConfidence() > 50);
    }
    
    @Test
    public void testConfidenceZero() throws Exception {
        DetectedFace face = template.requestBody("openimaj:face-detection", OpenImajProducerTest.class.getResourceAsStream("rhiot.png"), DetectedFace.class);
        assertNull(face);
    }
    
    @Test
    public void testConfidenceHigherThanSample() throws Exception {
        DetectedFace face = template.requestBody("openimaj:face-detection?confidence=100", 
                OpenImajProducerTest.class.getResourceAsStream("test-faces.jpg"), DetectedFace.class);
        assertNull(face);
    }
    
    @Test
    public void testTwoFaces() throws Exception {
        List<DetectedFace> faces = template.requestBody("openimaj:face-detection", OpenImajProducerTest.class.getResourceAsStream("test-faces.jpg"), List.class);
        assertNotNull(faces);
        assertTrue(faces.size() == 2);
        assertTrue(faces.get(0).getConfidence() > 50);
        assertTrue(faces.get(1).getConfidence() > 50);
    }
    
    @Test
    public void testFKEFaceDetector(){
        KEDetectedFace face = template.requestBody("openimaj:face-detection?faceDetector=#fkeFaceDetector", 
                OpenImajProducerTest.class.getResourceAsStream("test-face.png"), KEDetectedFace.class);
        
        assertNotNull(face);
        assertTrue(face.getConfidence() > 50);
        FacialKeypoint keypoint = face.getKeypoint(FacialKeypoint.FacialKeypointType.EYE_LEFT_LEFT);
        assertNotNull(keypoint);
    }
}
