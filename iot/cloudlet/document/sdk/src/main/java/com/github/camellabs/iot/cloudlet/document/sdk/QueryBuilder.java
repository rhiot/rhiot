package com.github.camellabs.iot.cloudlet.document.sdk;

import java.io.Serializable;

public class QueryBuilder implements Serializable {

    private final Object query;

    private int page = 0;

    private int size = 25;

    private boolean sortAscending = true;

    private String[] orderBy = new String[0];

    public static QueryBuilder buildQuery(Object query) {
        return new QueryBuilder(query);
    }

    public QueryBuilder(Object query) {
        this.query = query;
    }

    public Object getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public QueryBuilder page(int page) {
        this.page = page;
        return this;
    }

    public int getSize() {
        return size;
    }

    public QueryBuilder size(int size) {
        this.size = size;
        return this;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public QueryBuilder sortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
        return this;
    }

    public String[] getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String[] orderBy) {
        this.orderBy = orderBy;
    }

    public QueryBuilder orderBy(String... orderBy) {
        this.orderBy = orderBy;
        return this;
    }

}