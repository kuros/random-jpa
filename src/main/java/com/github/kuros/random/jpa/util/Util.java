package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;

import java.lang.reflect.Field;
import java.util.List;

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
public class Util {

    public static String printValues(final Object object) {
        if (object == null) {
            return "";
        }

        final Class<?> type = object.getClass();
        final Field[] declaredFields = type.getDeclaredFields();
        final StringBuilder builder = new StringBuilder();
        builder.append("[");

        for (int i = 0; i < declaredFields.length; i++) {
            try {
                final Field declaredField = declaredFields[i];
                declaredField.setAccessible(true);
                final Object value = declaredField.get(object);
                builder.append(declaredField.getName())
                        .append(": ")
                        .append(value);
                if (i != declaredFields.length - 1) {
                    builder.append(", ");
                }
            } catch (final IllegalAccessException e) {
                throw new RandomJPAException(e);
            }
        }

        builder.append("]");

        return builder.toString();
    }

    public static String printEntityId(final Object object) {
        if (object == null) {
            return "";
        }

        final Class<?> type = object.getClass();
        final EntityTableMapping entityTableMapping = AttributeProvider.getInstance().get(type);
        final List<String> attributeIds = entityTableMapping.getAttributeIds();

        final StringBuilder builder = new StringBuilder();
        builder.append("[");

        for (int i = 0; i < attributeIds.size(); i++) {
            try {
                final String attribute = attributeIds.get(i);
                final Field declaredField = type.getDeclaredField(attribute);
                declaredField.setAccessible(true);
                final Object value = declaredField.get(object);
                builder.append(declaredField.getName())
                        .append(": ")
                        .append(value);
                if (i != attributeIds.size() - 1) {
                    builder.append(", ");
                }
            } catch (final Exception e) {
                throw new RandomJPAException(e);
            }
        }

        builder.append("]");

        return builder.toString();
    }
}
