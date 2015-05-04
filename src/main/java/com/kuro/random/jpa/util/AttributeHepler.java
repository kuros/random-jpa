package com.kuro.random.jpa.util;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;

/**
 * Created by Kumar Rohit on 5/3/15.
 */
public class AttributeHepler {

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
}
