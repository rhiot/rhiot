/// <reference path="../../includes.d.ts" />
declare module DataViewer {
    class Query {
        page: Number;
        size: Number;
        orderBy: string[];
        query: any[];
        sortAscending: Boolean;
        constructor(page?: number, size?: number, query?: QueryCondition[], orderBy?: string[], sortAscending?: Boolean);
        addOrderBy(order: string): void;
    }
    class QueryCondition {
        key: string;
        value: string;
        operator: Operator;
    }
    class Operator {
        static Equal: Operator;
        static GreaterThan: Operator;
        static GreaterThanEqual: Operator;
        static LessThan: Operator;
        static LessThanEqual: Operator;
        static getValues(): String[];
    }
}
