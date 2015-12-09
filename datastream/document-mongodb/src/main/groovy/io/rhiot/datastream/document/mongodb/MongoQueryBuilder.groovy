/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.datastream.document.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject

import static io.rhiot.utils.Maps.immutableMapOf;
import static java.lang.Boolean.parseBoolean

public class MongoQueryBuilder {

    private static final Map<String, String> SIMPLE_SUFFIX_OPERATORS = immutableMapOf(
            "GreaterThan", '$gt',
            "GreaterThanEqual", '$gte',
            "LessThan", '$lt',
            "LessThanEqual", '$lte',
            "NotIn", '$nin',
            "In", '$in');

    public DBObject jsonToMongoQuery(DBObject jsonQuery) {
        BasicDBObject mongoQuery = new BasicDBObject();
        for (String originalKey : jsonQuery.keySet()) {
            String compoundKey = originalKey.replaceAll('(.)_', '$1.');

            String suffixOperator = findFirstMatchOperator(originalKey);
            if (suffixOperator != null) {
                addRestriction(mongoQuery, compoundKey, suffixOperator, SIMPLE_SUFFIX_OPERATORS.get(suffixOperator), jsonQuery.get(originalKey));
                continue;
            }

            if (originalKey.endsWith("Contains")) {
                addRestriction(mongoQuery, compoundKey, "Contains", '$regex', ".*" + jsonQuery.get(originalKey) + ".*");
            } else {
                mongoQuery.put(compoundKey, new BasicDBObject('$eq', jsonQuery.get(originalKey)));
            }
        }
        return mongoQuery;
    }

    // TODO Don't return stack traces
    public DBObject queryBuilderToSortConditions(Map<String, Object> queryBuilder) {
        int order = parseBoolean(queryBuilder.getOrDefault("sortAscending", true).toString()) ? 1 : -1;
        List<String> orderBy = (List<String>) queryBuilder.getOrDefault("orderBy", Collections.emptyList()); // Suggest that it should list here
        if (orderBy.size() == 0) {
            return new BasicDBObject('$natural', order);
        } else {
            BasicDBObject sort = new BasicDBObject();
            for (String by : orderBy) {
                sort.put(by, order);
            }
            return sort;
        }
    }

    // Helpers

    private String findFirstMatchOperator(String originalKey) {
        List<String> matchingSuffixOperators = SIMPLE_SUFFIX_OPERATORS.keySet().findAll{originalKey.endsWith(it)}.toList()
        return matchingSuffixOperators.isEmpty() ? null : matchingSuffixOperators.get(0);
    }

    private void addRestriction(BasicDBObject query, String propertyWithOperator, String propertyOperator, String operator, Object value) {
        String property = propertyWithOperator.replaceAll(propertyOperator + '$', "");
        if (query.containsField(property)) {
            BasicDBObject existingRestriction = (BasicDBObject) query.get(property);
            existingRestriction.put(operator, value);
        } else {
            query.put(property, new BasicDBObject(operator, value));
        }
    }

}
