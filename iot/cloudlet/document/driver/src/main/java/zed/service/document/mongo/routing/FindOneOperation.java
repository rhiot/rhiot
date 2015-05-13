package zed.service.document.mongo.routing;

public class FindOneOperation {

    private final String collection;

    private final String id;

    public FindOneOperation(String collection, String id) {
        this.collection = collection;
        this.id = id;
    }

    public String collection() {
        return collection;
    }

    public String id() {
        return id;
    }

}
