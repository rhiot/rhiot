package com.github.camellabs.iot.cloudlet.document.sdk;

import java.util.List;

public interface DocumentService<T> {

    T save(T document);

    T findOne(Class<T> documentClass, String id);

    List<T> findMany(Class<T> documentClass, String... ids);

    long count(Class<?> documentClass);

    List<T> findByQuery(Class<T> documentClass, QueryBuilder queryBuilder);

    long countByQuery(Class<T> documentClass, QueryBuilder queryBuilder);

    void remove(Class<T> documentClass, String id);

}
