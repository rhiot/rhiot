package io.rhiot.component.kura.test;

import io.rhiot.component.kura.router.RhiotKuraRouter;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kura.KuraRouter;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;

public class TestKuraServerTest {

	@Test
	public void shouldStartRoute() throws InterruptedException {
		// Given
		TestKuraServer kuraServer = new TestKuraServer();
		KuraRouter router = kuraServer.start(TestKuraRouter.class);
		MockEndpoint mock = router.getContext().getEndpoint("mock:out", MockEndpoint.class);
		mock.expectedMessageCount(1);
		ProducerTemplate template = router.getContext().createProducerTemplate();

		// When
		template.sendBody("direct:start", "This is a test message");

		// Then
		assertIsSatisfied(mock);
	}

	static class TestKuraRouter extends RhiotKuraRouter {

		@Override
		public void configure() throws Exception {
			from("direct:start").to("log:org.apache.camel.component.kura.TestKuraRouter").to("mock:out");
		}

	}

}