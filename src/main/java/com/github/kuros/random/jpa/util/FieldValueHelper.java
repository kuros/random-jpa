package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.mapper.TableNode;
import com.github.kuros.random.jpa.mapper.FieldValue;

import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/8/15.
 */
public class FieldValueHelper {

    public static TableNode getTableNode(final FieldValue fieldValue) {
        final Field field = fieldValue.getField();
        final TableNode tableNode = TableNode.newInstance();
        return tableNode;
    }

    public static Class<?> getDeclaringClass(final FieldValue fieldValue) {
        final Field field = fieldValue.getField();
        return field.getDeclaringClass();
    }
}
