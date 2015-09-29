package io.rhiot.component.gps.bu353.converter;

import io.rhiot.component.gps.bu353.ClientGpsCoordinates;
import org.apache.camel.Converter;

@Converter
public class ClientGpsCoordinatesConverter {

    @Converter
    public static String toString(ClientGpsCoordinates clientGpsCoordinates) {
        return clientGpsCoordinates.serialize();
    }
    
    @Converter
    public static ClientGpsCoordinates toClientGpsCoordinates(String serializedCoordinates) {
        return ClientGpsCoordinates.deserialize(serializedCoordinates);
    }
}
