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
package io.rhiot.datastream.node

import static com.google.common.truth.Truth.assertThat
import static org.mockito.Matchers.any
import static org.mockito.Matchers.eq
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

import io.rhiot.cloudplatform.runtime.spring.test.CloudPlatformTest

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient

@Configuration
class PaaSProtocolStompOverWebsocketAdaptersTest extends CloudPlatformTest {


    WebSocketClient transport = null;
    WebSocketStompClient stompClient = null;
    StompSession stompSession = null;

    static String getWebsocketUrl() {
        "ws://127.0.0.1:" + websocketPort;
    }

    @Before
    void unitBefore() {
        transport = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new StringMessageConverter());
    }

    @After
    void unitAfter() {
        stompSession.disconnect();
        assertFalse(stompSession.isConnected());
    }

    // WebSocket tests
    @Test(timeout = 20000L)
    void smokeTestWebsocketConnectionStreamConsumer() {

        StompSessionHandlerAdapter mock = Mockito.mock(StompSessionHandlerAdapter.class);
        ListenableFuture<StompSession> listenable = stompClient.connect(getWebsocketUrl(), mock);
        stompSession = listenable.get();
        sleep(1000L);
        verify(mock).afterConnected(any(),any());
    }

    @Test(timeout = 20000L)
    void smokeTestWebsocketSubscribeStreamConsumer() {

        StompSessionHandlerAdapter mockHandler = Mockito.mock(StompSessionHandlerAdapter.class);
        StompFrameHandler mockFrameHandler = Mockito.mock(StompFrameHandler.class);
        when(mockFrameHandler.getPayloadType(any())).thenReturn(String.class);

        ListenableFuture<StompSession> listenable = stompClient.connect(getWebsocketUrl(), mockHandler);
        stompSession = listenable.get();

        sleep(1000L);

        stompSession.send("document.save.WsDoc", payloadEncoding.encode([foo: 'bar']));

        sleep(1000L);

        // When
        def count = connector.fromBus("document.count.WsDoc", int.class)

        verify(mockHandler).afterConnected(any(),any());

        // Then
        assertThat(count).isEqualTo(1)

    }

}