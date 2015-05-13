package zed.service.document.mongo.crossstore.sql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.reflect.FieldUtils.readField;

@Component
public class DefaultPropertiesResolver implements PropertiesResolver {

    private final Set<?> basicTypes = Sets.newHashSet(String.class, Long.class, Integer.class, BigDecimal.class, BigInteger.class);

    @Override
    public <T> T readFrom(Object from, Property<T> property) {
        try {
            return (T) readField(from, property.name(), true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Property> resolveProperties(Class<?> pojoClass) {
        List<Property> basicProperties = resolveBasicProperties(pojoClass);
        List<Property> pojoProperties = resolvePojoProperties(pojoClass);
        return ImmutableList.<Property>builder().addAll(basicProperties).addAll(pojoProperties).build();
    }

    @Override
    public List<Property> resolveBasicProperties(Class<?> pojoClass) {
        final List<Property> properties = Lists.newLinkedList();
        ReflectionUtils.doWithFields(pojoClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                properties.add(new Property(field.getName(), field.getType()));
            }
        }, new ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return basicTypes.contains(field.getType());
            }
        });
        return properties;
    }

    @Override
    public List<Property> resolvePojoProperties(Class<?> pojoClass) {
        final List<Property> properties = Lists.newLinkedList();
        ReflectionUtils.doWithFields(pojoClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                properties.add(new Property(field.getName(), field.getType()));
            }
        }, new ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return !basicTypes.contains(field.getType());
            }
        });
        return properties;
    }

}