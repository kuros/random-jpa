package com.github.kuros.random.jpa.link;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.util.AttributeHelper;

import javax.persistence.Column;
import javax.persistence.metamodel.Attribute;
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
public class LinkAdapter {

    public static Relation adapt(final Link link) {
        final Field from = getFieldValue(link.getFrom());
        final Field to = getFieldValue(link.getFrom());
        return Relation.newInstance(from, to);
    }

    private static Field getFieldValue(final Attribute attribute) {
        final Class declaringClass = AttributeHelper.getDeclaringClass(attribute);

        final String name = AttributeHelper.getName(attribute);

        return getField(declaringClass, name);

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
