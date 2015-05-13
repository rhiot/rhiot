package zed.service.document.mongo.crossstore.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Component
public class DynamicSchemaExpander {

    private final JdbcTemplate jdbcTemplate;

    private final PropertiesResolver propertiesResolver;

    @Autowired
    public DynamicSchemaExpander(JdbcTemplate jdbcTemplate, PropertiesResolver propertiesResolver) {
        this.jdbcTemplate = jdbcTemplate;
        this.propertiesResolver = propertiesResolver;
    }

    public void expandPojoSchema(Class<?> pojoClass) {
        expandPojoSchema(null, pojoClass);
    }

    private void expandPojoSchema(String parent, Class<?> pojoClass) {
        String pojoTableName = Pojos.pojoClassToCollection(pojoClass);
        if (parent != null) {
            pojoTableName = parent + "_" + pojoTableName;
        }

        boolean tableExists = true;
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + pojoTableName, Long.class);
        } catch (BadSqlGrammarException e) {
            tableExists = false;
        }

        Map<String, Class<?>> pojoTableSchema = new HashMap<>();
        for (Property property : propertiesResolver.resolveBasicProperties(pojoClass)) {
            if (property.type() == String.class) {
                pojoTableSchema.put(", " + property.name() + " VARCHAR(1024)", property.type());
            } else if (property.type() == Integer.class) {
                pojoTableSchema.put(", " + property.name() + " LONG", property.type());
            } else if (property.type() == Long.class) {
                pojoTableSchema.put(", " + property.name() + " LONG", property.type());
            } else if (property.type() == BigDecimal.class) {
                pojoTableSchema.put(", " + property.name() + " DECIMAL", property.type());
            } else {
                throw new IllegalStateException("Unknown basic type: " + property.type());
            }
        }

        for (Property property : propertiesResolver.resolvePojoProperties(pojoClass)) {
            expandPojoSchema(pojoTableName, property.type());
        }

        if (!tableExists) {
            String createSql = String.format("CREATE TABLE %s (id VARCHAR(128)", pojoTableName);
            if (parent != null) {
                createSql += ", " + parent + "_id LONG";
            }
            for (String column : pojoTableSchema.keySet()) {
                createSql += column;
            }
            createSql += ")";
            jdbcTemplate.execute(createSql);
        }
    }

}
