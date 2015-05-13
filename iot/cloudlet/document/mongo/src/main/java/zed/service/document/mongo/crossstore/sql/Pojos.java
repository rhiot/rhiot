package zed.service.document.mongo.crossstore.sql;

public final class Pojos {

    private Pojos() {
    }

    public static String pojoClassToCollection(Class<?> pojoClass) {
        return pojoClass.getSimpleName();
    }

}
