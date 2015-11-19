package io.rhiot.component.kura;

import org.apache.camel.component.kura.KuraRouter;
import org.apache.felix.connect.launch.PojoServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojosrKuraServer {
    private static Logger log = LoggerFactory.getLogger(PojosrKuraServer.class);
    private PojoServiceRegistry registry;

    public PojosrKuraServer() {
    }

    public <T extends KuraRouter> T start(Class<T> kuraRouter) {
        registry = PojosrRegistry.getInstance().getRegistry();
        T router = null;
        try {
            log.debug("Starting router for class {}...", kuraRouter.getName());
            router = kuraRouter.newInstance();
            router.start(registry.getBundleContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return router;
    }
}
