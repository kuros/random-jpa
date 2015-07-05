package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.exception.RandomJPAException;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
public class AttributeHelper {

    public static Class<?> getDeclaringClass(final Attribute<?, ?> attribute) {
        return attribute.getDeclaringType().getJavaType();
    }

    public static String getName(final Attribute<?, ?> attribute) {
        return attribute.getJavaMember().getName();
    }

    public static Class<?> getAttributeClass(final Attribute<?, ?> attribute) {
        if (attribute == null) {
            throw new NullPointerException("Attribute cannot be null");
        }
        if (attribute instanceof PluralAttribute) {
            return ((PluralAttribute)attribute).getBindableJavaType();
        }
        return attribute.getJavaType();
    }

    public static Field getField(final Attribute<? , ?> attribute) throws NoSuchFieldException {

        if (attribute == null) {
            throw new NullPointerException("Attribute cannot be null");
        }

        return attribute.getJavaMember().getDeclaringClass().getDeclaredField(getName(attribute));
    }

    public static List<Field> getFields(final List<Attribute<?, ?>> attributes) {
        final List<Field> fields = new ArrayList<Field>();
        for (Attribute<?, ?> attribute : attributes) {
            try {
                fields.add(AttributeHelper.getField(attribute));
            } catch (final NoSuchFieldException e) {
                throw new RandomJPAException(e);
            }
        }

        return fields;
    }
}
