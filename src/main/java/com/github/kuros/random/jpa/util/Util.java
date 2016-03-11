package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;

import java.lang.reflect.Field;
import java.text.MessageFormat;
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
                final String s = String.valueOf(value);
                builder.append(declaredField.getName())
                        .append(": ")
                        .append(s);
                if (i != declaredFields.length - 1) {
                    builder.append(", ");
                }
            } catch (final Exception e) {
                // Skip
            }
        }

        builder.append("]");

        return builder.toString();
    }

    public static String printEntityId(final Cache cache, final Object object) {
        if (object == null) {
            return "";
        }

        try {
            final Class<?> type = object.getClass();
            final EntityTableMapping entityTableMapping = cache.getAttributeProvider().get(type);
            final List<String> attributeIds = entityTableMapping.getAttributeIds();

            final StringBuilder builder = new StringBuilder();
            builder.append("[");

            for (int i = 0; i < attributeIds.size(); i++) {
                try {
                    final String attribute = attributeIds.get(i);
                    final Field declaredField = getField(type, attribute);
                    declaredField.setAccessible(true);
                    final Object value = declaredField.get(object);
                    builder.append(declaredField.getName())
                            .append(": ")
                            .append(value);
                    if (i != attributeIds.size() - 1) {
                        builder.append(", ");
                    }
                } catch (final Exception e) {
                    //do nothing
                }
            }

            builder.append("]");

            return builder.toString();
        } catch (final Exception e) {
            return "";
        }
    }

    public static Field getField(final Class<?> tableClass, final String attribute) throws NoSuchFieldException {
        try {
            return tableClass.getDeclaredField(attribute);
        } catch (final NoSuchFieldException e) {
            if (tableClass.getSuperclass() != Object.class) {
                return getField(tableClass.getSuperclass(), attribute);
            }
            throw e;
        }
    }

    public static String formatMessage(final String message, final Object[] args) {
        return new MessageFormat(message).format(args);
    }
}
