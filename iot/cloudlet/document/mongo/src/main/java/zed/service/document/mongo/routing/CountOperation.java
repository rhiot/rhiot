package zed.service.document.mongo.routing;

public class CountOperation {

    private final String collection;

    public CountOperation(String collection) {
        this.collection = collection;
    }

    public String collection() {
        return collection;
    }

}