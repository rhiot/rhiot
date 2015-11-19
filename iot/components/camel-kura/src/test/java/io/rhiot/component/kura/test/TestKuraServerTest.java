package io.rhiot.component.kura.test;

import io.rhiot.component.kura.router.RhiotKuraRouter;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kura.KuraRouter;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

public class TestKuraServerTest {

	@Test
	public void shouldStartRoute() throws InterruptedException {
		TestKuraServer pks = new TestKuraServer();
		KuraRouter kr = pks.start(TestKuraRouter.class);

		MockEndpoint out = (MockEndpoint) kr.endpoint("mock:out");

		ProducerTemplate template = kr.getContext().createProducerTemplate();
		template.sendBody("direct:start", "This is a test message");

		out.expectedMessageCount(1);

		MockEndpoint.assertIsSatisfied(out);
	}

	static class TestKuraRouter extends RhiotKuraRouter {

		@Override
		public void configure() throws Exception {
			from("direct:start").to("log:org.apache.camel.component.kura.TestKuraRouter").to("mock:out");
		}

	}

}