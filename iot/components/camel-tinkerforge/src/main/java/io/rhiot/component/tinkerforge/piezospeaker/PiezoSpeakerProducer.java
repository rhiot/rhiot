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
package io.rhiot.component.tinkerforge.piezospeaker;

import java.io.IOException;

import org.apache.camel.Exchange;

import io.rhiot.component.tinkerforge.TinkerforgeProducer;
import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletPiezoSpeaker;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

public class PiezoSpeakerProducer extends TinkerforgeProducer<PiezoSpeakerEndpoint, BrickletPiezoSpeaker> {

    public PiezoSpeakerProducer(PiezoSpeakerEndpoint endpoint) throws IOException, AlreadyConnectedException {
        super(endpoint, BrickletPiezoSpeaker.DEVICE_IDENTIFIER);
        System.out.println("PiezoSpeakerProducer()");
    }

    @Override
    protected BrickletPiezoSpeaker createBricklet(String uid, IPConnection connection) {
        System.out.println("createBricklet()");
        return new BrickletPiezoSpeaker(uid, connection);
    }

    @Override
    protected void configureBricklet() throws TimeoutException, NotConnectedException {
        System.out.println("configureBricklet()");
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println("process()");
        Integer frequency = exchange.getIn().getHeader("frequency", endpoint.getFrequency(), Integer.class);
        Integer duration = exchange.getIn().getHeader("duration", endpoint.getDuration(), Integer.class);

        bricklet.beep(duration, frequency);
    	log.debug("Started beep with duration "+duration+"ms and frequency "+frequency+"hz");
    }
}
