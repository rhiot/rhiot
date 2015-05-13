package zed.service.document.mongo.crossstore.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CrossStoreStatementsGenerator {

    private final DynamicSchemaExpander dynamicSchemaExpander;

    private final PropertiesResolver propertiesResolver;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CrossStoreStatementsGenerator(DynamicSchemaExpander dynamicSchemaExpander, PropertiesResolver propertiesResolver, JdbcTemplate jdbcTemplate) {
        this.dynamicSchemaExpander = dynamicSchemaExpander;
        this.propertiesResolver = propertiesResolver;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(String oid, Object pojo) {
        dynamicSchemaExpander.expandPojoSchema(pojo.getClass());
        doInsert(oid, null, null, null, pojo);
    }

    public void doInsert(String oid, String parentOid, Long parentId, String parentTable, Object pojo) {
        String table = Pojos.pojoClassToCollection(pojo.getClass());
        if (parentTable != null) {
            table = parentTable + "_" + table;
        }

        String insertStatement = "INSERT INTO " + table + " (id";
        for (Property property : propertiesResolver.resolveBasicProperties(pojo.getClass())) {
            insertStatement += ", " + property.name();
        }
        insertStatement += ") VALUES ('" + oid + "'";
        for (Property property : propertiesResolver.resolveBasicProperties(pojo.getClass())) {
            insertStatement += ", ";
            if (property.type() == String.class) {
                insertStatement += "'" + propertiesResolver.readFrom(pojo, property) + "'";
                } else {
                insertStatement += propertiesResolver.readFrom(pojo, property);
                }
        }
        insertStatement += ")";
        jdbcTemplate.execute(insertStatement);

        for (Property<?> property : propertiesResolver.resolvePojoProperties(pojo.getClass())) {
            Object nestedPojo = propertiesResolver.readFrom(pojo, property);
            if (parentId == null) {
                doInsert(null, oid, null, table, nestedPojo);
            } else {
                long insertedId = 1;
                doInsert(null, null, insertedId, table, nestedPojo);
            }
        }
    }

}
