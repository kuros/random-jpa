package com.kuro.random.jpa.util;

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
            if(column != null && (column.name().equals(columnName) || declaredField.getName().equals(columnName))) {
                return declaredField;
            }
        }

        throw new ColumnNotMappedException("Column Mapping Not found in " + tableClass.getName() + " for " + columnName);
    }

    public static <T> Class<T> getClass(final EntityType<T> entityType) {
        return entityType.getJavaType();
    }

    private static class ColumnNotMappedException extends RuntimeException {
        public ColumnNotMappedException(final String message) {
            super(message);
        }
    }
}
