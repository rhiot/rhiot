package io.rhiot.datastream.camel.rest

import com.google.common.truth.Truth
import io.rhiot.datastream.engine.AbstractServiceStreamConsumer
import io.rhiot.datastream.engine.AbstractServiceStreamSource
import io.rhiot.datastream.engine.DataStream
import io.rhiot.steroids.Bean
import io.vertx.core.json.Json
import org.junit.Test

class CamelRestServiceStreamSourceTest {

    static def dataStream = new DataStream().start()

    @Test
    void shouldInvokeGetOperation() {
        def response = Json.mapper.readValue(new URL('http://localhost:8080/test/count/1'), Map.class)
        Truth.assertThat(response.result).isEqualTo(1)
    }

    static interface TestService {

        int count(int number)

    }

    @Bean
    static class TestInterfaceImpl implements TestService {

        @Override
        int count(int number) {
            number
        }

    }

    static class TestInterfaceCamelRestStreamSource extends CamelRestServiceStreamSource<TestService> {

        TestInterfaceCamelRestStreamSource() {
            super(TestService.class, 'test')
        }

    }

    static class TestInterfaceStreamConsumer extends AbstractServiceStreamConsumer<TestService> {

        TestInterfaceStreamConsumer() {
            super('test', TestService.class)
        }
    }

}
