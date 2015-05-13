package boot.mongo;

import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;

public class MongoDbMvcEndpoint extends EndpointMvcAdapter {

    public MongoDbMvcEndpoint(MongoDbEndpoint delegate) {
        super(delegate);
    }

}
