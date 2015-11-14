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
package io.rhiot.component.framebuffer;

import java.util.Random;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Main {

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {
				Processor process = new Snake();

				from("timer:name?repeatCount=128000&period=10").process(process).to("framebuffer://RPi-Sense FB");
			}
		});

		context.start();
		Thread.sleep(1000000);
		context.stop();
	}

}

class ImageRandom implements Processor {

	static int XY = 8;
	static int BPP = 2;
	byte[] buffer = new byte[XY * XY * BPP];
	int i;

	public ImageRandom() {
	}

	@Override
	public void process(Exchange arg0) throws Exception {

		Random r = new Random();
		int x = r.nextInt(XY);
		int y = r.nextInt(XY);
		if (i == XY * XY) {
			i = 0;
		}
		// buffer[x + y * 8] = (byte) r.nextInt(255);
		// buffer[x + y * 8 + 1] = (byte) r.nextInt(255);
		// buffer[x + y * 8 + 2] = (byte) r.nextInt(255);
		if (buffer[x * BPP + y * XY * BPP] == 0x00 || buffer[x * BPP + y * XY * BPP + 1] == 0x00) {
			buffer[x * BPP + y * XY * BPP] = (byte) r.nextInt(255);
			buffer[x * BPP + y * XY * BPP + 1] = (byte) r.nextInt(255);
		} else {
			buffer[x * BPP + y * XY * BPP] = (byte) 0x00;
			buffer[x * BPP + y * XY * BPP + 1] = (byte) 0x00;
		}

		// buffer[i * 2] = (byte) 0xff;
		// buffer[i * 2 + 1] = (byte) 0xff;
		i++;

		System.out.println(x + " " + y);
		arg0.getIn().setBody(buffer.clone());
	}
}

class Snake implements Processor {

	static int XY = 8;
	static int BPP = 2;
	byte[] buffer = new byte[XY * XY * BPP];
	int i;

	public Snake() {
	}

	@Override
	public void process(Exchange arg0) throws Exception {

		if (i == XY * XY) {
			i = 0;
		}

		if (buffer[i * 2] == 0x00 || buffer[i * 2 + 1] == 0x00) {
			buffer[i * 2] = (byte) 0xff;
			buffer[i * 2 + 1] = (byte) 0xff;
		} else {
			buffer[i * 2] = (byte) 0x00;
			buffer[i * 2 + 1] = (byte) 0x00;
		}

		i++;

		arg0.getIn().setBody(buffer.clone());
	}
}