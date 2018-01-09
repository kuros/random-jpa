package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collection;
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

                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append(declaredField.getName())
                            .append(": ")
                            .append(value);
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

    public static String formatMessage(final String message, final Object... args) {
        return new MessageFormat(message).format(args);
    }

    public static void assertNotNull(final String message, final Object obj) {
        if (obj == null) {
            throw new RandomJPAException(message);
        }
    }

    public static Object getFieldValue(final Object object, final Field field) {
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
            final PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (pd.getName().equals(field.getName())) {
                    final Method getter = pd.getReadMethod();
                    return getter.invoke(object);
                }
            }

            field.setAccessible(true);
            return field.get(object);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertToString(final Collection<?> objects) {
        String str = "[";
        boolean isNotFirst = false;
        for (Object object : objects) {
            if (isNotFirst) {
                str += ", ";
            }
            str += object;
            isNotFirst = true;
        }
        str += "]";
        return str;
    }

    public static Object invokeMethod(final Object object, final String name, final Object... params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class<?>[] paramClasses = new Class[params.length];

        for (int i = 0; i < params.length; i++) {
            paramClasses[i] = params[i].getClass();
        }
        final Class<?> aClass = object.getClass();
        final Method method = getMethod(name, aClass, paramClasses);
        final Object invoke;
        if (method.isAccessible()) {
            invoke = method.invoke(object, params);
        } else {
            method.setAccessible(true);
            invoke = method.invoke(object, params);
            method.setAccessible(false);
        }

        return invoke;
    }

    private static Method getMethod(final String methodName, final Class<?> objClass, final Class<?>[] paramClasses) throws NoSuchMethodException {
        if (objClass == Object.class) {
            return objClass.getDeclaredMethod(methodName, paramClasses);
        }

        try {
            return objClass.getDeclaredMethod(methodName, paramClasses);
        } catch (final NoSuchMethodException e) {
            return getMethod(methodName, objClass.getSuperclass(), paramClasses);
        }
    }
}
