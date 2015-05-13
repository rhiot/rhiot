package com.github.camellabs.iot.cloudlet.document.driver.query;

import com.google.common.base.Predicate;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.camellabs.iot.utils.Maps.immutableMapOf;
import static com.google.common.collect.Iterables.filter;
import static java.lang.Boolean.parseBoolean;

public class MongoQueryBuilder {

    private static final Map<String, String> SIMPLE_SUFFIX_OPERATORS = immutableMapOf(
            "GreaterThan", "$gt",
            "GreaterThanEqual", "$gte",
            "LessThan", "$lt",
            "LessThanEqual", "$lte",
            "NotIn", "$nin",
            "In", "$in");

    public DBObject jsonToMongoQuery(DBObject jsonQuery) {
        BasicDBObject mongoQuery = new BasicDBObject();
        for (String originalKey : jsonQuery.keySet()) {
            String compoundKey = originalKey.replace('_', '.');

            String suffixOperator = findFirstMatchOperator(originalKey);
            if (suffixOperator != null) {
                addRestriction(mongoQuery, compoundKey, suffixOperator, SIMPLE_SUFFIX_OPERATORS.get(suffixOperator), jsonQuery.get(originalKey));
                continue;
            }

            if (originalKey.endsWith("Contains")) {
                addRestriction(mongoQuery, compoundKey, "Contains", "$regex", ".*" + jsonQuery.get(originalKey) + ".*");
            } else {
                mongoQuery.put(compoundKey, new BasicDBObject("$eq", jsonQuery.get(originalKey)));
            }
        }
        return mongoQuery;
    }

    // TODO Don't return stack traces
    public DBObject queryBuilderToSortConditions(Map<String, Object> queryBuilder) {
        int order = parseBoolean(queryBuilder.get("sortAscending").toString()) ? 1 : -1; // TODO Use defaults
        List<String> orderBy = (List<String>) queryBuilder.get("orderBy");  // TODO Use defaults // Suggest that it should list here
        if (orderBy.size() == 0) {
            return new BasicDBObject("$natural", order);
        } else {
            BasicDBObject sort = new BasicDBObject();
            for (String by : orderBy) {
                sort.put(by, order);
            }
            return sort;
        }
    }

    private String findFirstMatchOperator(String originalKey) {
        Iterator<String> matchingSuffixOperators = filter(SIMPLE_SUFFIX_OPERATORS.keySet(), new Predicate<String>() {
            @Override
            public boolean apply(String suffixOperator) {
                return originalKey.endsWith(suffixOperator);
            }
        }).iterator();
        return matchingSuffixOperators.hasNext() ? matchingSuffixOperators.next() : null;
    }

    private void addRestriction(BasicDBObject query, String propertyWithOperator, String propertyOperator, String operator, Object value) {
        String property = propertyWithOperator.replaceAll(propertyOperator + "$", "");
        if (query.containsField(property)) {
            BasicDBObject existingRestriction = (BasicDBObject) query.get(property);
            existingRestriction.put(operator, value);
        } else {
            query.put(property, new BasicDBObject(operator, value));
        }
    }

}
