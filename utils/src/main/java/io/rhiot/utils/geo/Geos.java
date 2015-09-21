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
package io.rhiot.utils.geo;

import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Double.parseDouble;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public final class Geos {

    private final static Logger LOG = getLogger(Geos.class);

    private Geos() {
    }

    /**
     * Converts Degrees Decimal Minutes GPS coordinate string into the decimal value.
     *
     * @param ddmCoordinate Degrees Decimal Minutes GPS coordinate to convert. Can't be null nor blank.
     * @return GPS coordinates in decimal format
     */
    public static double convertDdmCoordinatesToDecimal(String ddmCoordinate) {
        checkArgument(isNotBlank(ddmCoordinate), "Coordinate to convert can't be null nor blank.");

        int dotIndex = ddmCoordinate.indexOf('.');
        double degrees = parseDouble(ddmCoordinate.substring(0, dotIndex - 2));
        double minutes = parseDouble(ddmCoordinate.substring(dotIndex - 2));
        double decimalCoordinates = degrees + minutes/60;
        LOG.debug("Converted NMEA DDM degree coordinate {} (degrees/minutes: {}/{}) to decimal coordinate {}.", ddmCoordinate, degrees, minutes, decimalCoordinates);
        return decimalCoordinates;
    }

}
