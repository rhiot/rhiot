package com.github.camellabs.iot.cloudlet.document.driver.mongodb;

import org.apache.camel.Exchange;
import org.apache.camel.support.ExpressionAdapter;
import com.github.camellabs.iot.cloudlet.document.driver.routing.FindByQueryOperation;

public class MongoDbSortConditionExpression extends ExpressionAdapter {

    public static MongoDbSortConditionExpression sortCondition() {
        return new MongoDbSortConditionExpression();
    }

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        FindByQueryOperation operation = exchange.getIn().getBody(FindByQueryOperation.class);
        return (T) new MongoQueryBuilder().queryBuilderToSortConditions(operation.queryBuilder());
    }

}
