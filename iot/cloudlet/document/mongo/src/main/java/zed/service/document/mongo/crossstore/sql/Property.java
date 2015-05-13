package zed.service.document.mongo.crossstore.sql;

public class Property<T> {

    private final String name;

    private final Class<T> type;

    public Property(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }


    public String name() {
        return name;
    }

    public Class<T> type() {
        return type;
    }

}
