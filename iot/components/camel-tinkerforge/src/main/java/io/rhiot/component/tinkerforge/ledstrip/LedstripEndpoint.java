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
package io.rhiot.component.tinkerforge.ledstrip;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

import io.rhiot.component.tinkerforge.TinkerforgeComponent;
import io.rhiot.component.tinkerforge.TinkerforgeEndpoint;
import com.tinkerforge.BrickletLEDStrip;

@UriEndpoint(scheme = "tinkerforge", syntax = "tinkerforge:/ledstrip/<uid>", producerOnly=true, label = "iot", title = "Tinkerforge")
public class LedstripEndpoint extends TinkerforgeEndpoint {
    
    @UriParam private int chipType = BrickletLEDStrip.CHIP_TYPE_WS2801;
    @UriParam private int frameDuration = 50;
    @UriParam private int amountOfLeds = 50;
    @UriParam private boolean throwExceptions = true;
    @UriParam private String rgbPattern = "rgb";
    @UriParam private String modus = LedStripModus.LedStrip;
    @UriParam private String layout = "30x5";

    private Producer producer;

    public LedstripEndpoint(String uri, TinkerforgeComponent tinkerforgeComponent) {
        super(uri, tinkerforgeComponent);
	}

    @Override
	public Producer createProducer() throws Exception {
        return producer != null ? producer : (producer = new LedstripProducer(this));
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new RuntimeCamelException("Cannot create a consumer object since the brickletType 'ledstrip' has no input sensors.");
    }

    public int getChipType() {
        return chipType;
    }

    public void setChipType(int chipType) {
        this.chipType = chipType;
    }

    public int getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(int frameDuration) {
        this.frameDuration = frameDuration;
    }

    public boolean getThrowExceptions() {
        return throwExceptions;
    }

    public void setThrowExceptions(boolean throwExceptions) {
        this.throwExceptions = throwExceptions;
    }

    public int getAmountOfLeds() {
        return amountOfLeds;
    }

    public void setAmountOfLeds(int amountOfLeds) {
        this.amountOfLeds = amountOfLeds;
    }

    public String getRgbPattern() {
        return rgbPattern;
    }

    public void setRgbPattern(String rgbPattern) {
        this.rgbPattern = rgbPattern;
    }

    public String getModus() {
        return modus;
    }

    public void setModus(String modus) {
        this.modus = modus;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
}