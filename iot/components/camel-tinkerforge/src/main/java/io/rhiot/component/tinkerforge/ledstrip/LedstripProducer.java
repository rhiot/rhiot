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

import com.tinkerforge.*;
import com.tinkerforge.TimeoutException;
import io.rhiot.component.tinkerforge.TinkerforgeProducer;
import io.rhiot.component.tinkerforge.ledstrip.matrix.LedCharacter;
import io.rhiot.component.tinkerforge.ledstrip.matrix.LedCharacterFactory;
import io.rhiot.component.tinkerforge.ledstrip.matrix.LedLayout;
import io.rhiot.component.tinkerforge.ledstrip.matrix.LedLayoutFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

public class LedstripProducer extends TinkerforgeProducer<LedstripEndpoint, BrickletLEDStrip> {
    private static final short FRAME_LENGTH = 16;
    private ConcurrentMap<Integer, LedData> ledDataMap = new ConcurrentHashMap<>(endpoint.getAmountOfLeds());
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private boolean thereIsNewData = false;

    public LedstripProducer(final LedstripEndpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletLEDStrip.DEVICE_IDENTIFIER);
    }

    @Override
    public BrickletLEDStrip createBricklet(String uid, IPConnection connection) {
        return new BrickletLEDStrip(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        bricklet.setChipType(endpoint.getChipType());
        bricklet.setFrameDuration(endpoint.getFrameDuration());
        bricklet.addFrameRenderedListener(new BrickletLEDStrip.FrameRenderedListener() {
            @Override
            public void frameRendered(int length) {
                if (thereIsNewData) sendLedData();
            }
        });

        // Send some irrelevant data to trigger the frameRendering callbacks
        scheduleLedData(0, new LedData());
        sendLedData();
    }

    private void sendLedData() {
        thereIsNewData = false;

        if (bricklet == null) {
            log.warn("No data could be send to the ledstrip...");
            return;
        }

        for (int i=0; i<endpoint.getAmountOfLeds(); i+= FRAME_LENGTH) {

            short[] r = new short[FRAME_LENGTH];
            short[] g = new short[FRAME_LENGTH];
            short[] b = new short[FRAME_LENGTH];

            for (int j=0; (j< FRAME_LENGTH) && ((j+i) < endpoint.getAmountOfLeds()); j++) {
                LedData data = ledDataMap.get(i+j);
                if (data != null) {
                    if (endpoint.getRgbPattern().equals("rgb")) {
                        r[j] = data.red;
                        g[j] = data.green;
                        b[j] = data.blue;
                    } else if (endpoint.getRgbPattern().equals("bgr")) {
                        r[j] = data.blue;
                        g[j] = data.green;
                        b[j] = data.red;
                    } else if (endpoint.getRgbPattern().equals("brg")) {
                        r[j] = data.blue;
                        g[j] = data.red;
                        b[j] = data.green;
                    } else {
                        throw new IllegalArgumentException("Unsupported RGB Pattern: "+endpoint.getRgbPattern());
                    }
                }
            }

            try {
                bricklet.setRGBValues(i, FRAME_LENGTH, r, g, b);
            } catch (TimeoutException | NotConnectedException e) {
                log.error("Could not send LED data", e);
            }
        }
    }

    @Override
    public void process(Exchange exchange) throws TimeoutException, NotConnectedException {
        if (endpoint.getThrowExceptions() && bricklet == null) {
            throw new IllegalStateException("The ledstrip is currently unreachable");
        }
        
        switch (endpoint.getModus()) {
            case LedStripModus.LedStrip : {
                Message message = exchange.getIn();
                LedData data = new LedData();
                data.red = message.getHeader("red", 0, Short.class);
                data.green = message.getHeader("green", 0, Short.class);
                data.blue = message.getHeader("blue", 0, Short.class);
                data.duration = message.getHeader("duration", 0, Integer.class);
                final Integer position = message.getHeader("position", Integer.class);

                if (position != null) {
                    scheduleLedData(position, data);
                } else {
                    for (int i=0; i<endpoint.getAmountOfLeds(); i++) {
                        scheduleLedData(i, data);
                    }
                }
                break;
            }
            case LedStripModus.CharacterMatrix : {
                Message message = exchange.getIn();
                String body = message.getBody(String.class);
                LedLayout layout = LedLayoutFactory.createLayout(endpoint.getLayout());
                List<LedCharacter> ledCharacters = LedCharacterFactory.getCharacters(body);
                List<Integer> resolvedLedNumbers = layout.mapCharacters(ledCharacters);
                
                for (int i=0; i<endpoint.getAmountOfLeds(); i++) {
                    if (resolvedLedNumbers.contains(i)) {
                        LedData data = new LedData();
                        data.red = message.getHeader("red", 0, Short.class);
                        data.green = message.getHeader("green", 0, Short.class);
                        data.blue = message.getHeader("blue", 0, Short.class);
                        data.duration = message.getHeader("duration", 0, Integer.class);
                        
                        scheduleLedData(i, data);
                    } else {
                        scheduleLedData(i, new LedData());
                    }
                }
            }
        }
    }

    private void scheduleLedData(final int position, LedData data) {
        ledDataMap.put(position, data);
        thereIsNewData = true;

        if (data.duration > 0) {
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    scheduleLedData(position, new LedData());
                }
            }, data.duration, TimeUnit.MILLISECONDS);
        }
    }

    private class LedData {
        int duration = 0;
        short red = 0;
        short green = 0;
        short blue = 0;
    }
}