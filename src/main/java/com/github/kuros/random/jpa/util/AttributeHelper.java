package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.exception.RandomJPAException;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.PluralAttribute;
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

    public static final String ATTRIBUTE_CANNOT_BE_NULL = "Attribute cannot be null, Please verify if Entity/Attribute declared properly";

    public static Class<?> getDeclaringClass(final Attribute<?, ?> attribute) {
        return attribute.getDeclaringType().getJavaType();
    }

    public static String getName(final Attribute<?, ?> attribute) {
        return attribute.getName();
    }

    public static Class<?> getAttributeClass(final Attribute<?, ?> attribute) {
        if (attribute == null) {
            throw new NullPointerException(ATTRIBUTE_CANNOT_BE_NULL);
        }
        if (attribute instanceof PluralAttribute<?,?,?> pluralAttribute) {
            return pluralAttribute.getBindableJavaType();
        }
        return attribute.getJavaType();
    }

    public static Field getField(final Attribute<?, ?> attribute) {

        if (attribute == null) {
            throw new NullPointerException(ATTRIBUTE_CANNOT_BE_NULL);
        }

        try {
            return attribute.getJavaMember().getDeclaringClass().getDeclaredField(getName(attribute));
        } catch (final NoSuchFieldException e) {
            throw new RandomJPAException("Attribute cannot be mapped to Field");
        }
    }

    public static List<Field> getFields(final List<Attribute<?, ?>> attributes) {
        final List<Field> fields = new ArrayList<>();
        for (Attribute<?, ?> attribute : attributes) {
            fields.add(AttributeHelper.getField(attribute));
        }

        return fields;
    }
}
