package io.rhiot.component.kura;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kura.KuraRouter;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

public class PojosrKuraServerTestUnit {

	@Test
	public void test() throws InterruptedException {
		PojosrKuraServer pks = new PojosrKuraServer();
		KuraRouter kr = pks.start(TestKuraRouter.class);

		MockEndpoint out = (MockEndpoint) kr.endpoint("mock:out");

		ProducerTemplate template = kr.getContext().createProducerTemplate();
		template.sendBody("direct:start", "This is a test message");

		out.expectedMessageCount(1);

		MockEndpoint.assertIsSatisfied(out);
	}

}

class TestKuraRouter extends KuraRouter {

	@Override
	public void configure() throws Exception {
		from("direct:start").to("log:org.apache.camel.component.kura.TestKuraRouter").to("mock:out");

	}

}