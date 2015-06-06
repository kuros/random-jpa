package com.github.kuros.random.jpa.util;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        return foundColumn(declaredField, column, columnName);
    }

    private static boolean foundColumn(final Field field, final Column column, final String columnName) {
        return column != null && (column.name().equals(columnName) || field.getName().equals(columnName));
    }

    public static <T> Class<T> getClass(final EntityType<T> entityType) {
        return entityType.getJavaType();
    }

}
