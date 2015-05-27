package com.github.kuros.random.jpa.link;

import com.github.kuros.random.jpa.mapper.FieldValue;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.util.AttributeHelper;

import javax.persistence.Column;
import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public class LinkAdapter {

    public static <T> Relation adapt(final Link link) {
        final FieldValue<T> from = getFieldValue(link.getFrom());
        final FieldValue<T> to = getFieldValue(link.getFrom());
        return Relation.newInstance(from, to);
    }

    private static <T> FieldValue<T> getFieldValue(final Attribute attribute) {
        final Class declaringClass = AttributeHelper.getDeclaringClass(attribute);

        final String name = AttributeHelper.getName(attribute);

        final Field field = getField(declaringClass, name);

        return FieldValue.newInstance(field);
    }

    private static Field getField(final Class tableClass, final String columnName) {
        Field field = null;
        final Field[] declaredFields = tableClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            final Column column = declaredField.getAnnotation(Column.class);
            if (column != null && (column.name().equals(columnName) || declaredField.getName().equals(columnName))) {
                field = declaredField;
                break;
            }
        }
        return field;
    }
}
