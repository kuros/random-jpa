package com.github.kuros.random.jpa.util;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/8/15.
 */
public class EntityTypeHelper {

    public static <T> Field getField(final EntityType<T> entityType, final String columnName) {
        final Class<T> tableClass = entityType.getJavaType();

        final Field[] declaredFields = tableClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            final Column column = declaredField.getAnnotation(Column.class);
            if (foundColumn(declaredField, column, columnName)) {
                return declaredField;
            }
        }

        for (Field declaredField : declaredFields) {
            final AttributeOverrides attributeOverrides = declaredField.getAnnotation(AttributeOverrides.class);
            if (attributeOverrides != null) {
                final AttributeOverride[] value = attributeOverrides.value();
                for (AttributeOverride attributeOverride : value) {
                    if (foundInAttributeOverride(declaredField, attributeOverride, columnName)) {
                        return declaredField;
                    }
                }
            }
        }

        for (Field declaredField : declaredFields) {
            final AttributeOverride attributeOverride = declaredField.getAnnotation(AttributeOverride.class);
            if (attributeOverride != null && foundInAttributeOverride(declaredField, attributeOverride, columnName)) {
                return declaredField;
            }
        }

        return null;
    }

    private static boolean foundInAttributeOverride(final Field declaredField, final AttributeOverride attributeOverride, final String columnName) {
        final Column column = attributeOverride.column();
        if (foundColumn(declaredField, column, columnName)) {
            return true;
        }
        return false;
    }

    private static boolean foundColumn(final Field field, final Column column, final String columnName) {
        return column != null && (column.name().equals(columnName) || field.getName().equals(columnName));
    }

    public static <T> Class<T> getClass(final EntityType<T> entityType) {
        return entityType.getJavaType();
    }

}
