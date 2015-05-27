package com.github.kuros.random.jpa.types;

import javax.persistence.metamodel.Attribute;

/**
 * Created by Kumar Rohit on 5/17/15.
 */
public final class AttributeValue<E, V> {

    private Attribute<E, V> attribute;
    private V value;

    private AttributeValue(final Attribute<E, V> attribute, final V value) {
        this.attribute = attribute;
        this.value = value;
    }

    public static <E, V> AttributeValue<E, V> newInstance(final Attribute<E, V> attribute, final V value) {
        return new AttributeValue<E, V>(attribute, value);
    }

    public Attribute<?, V> getAttribute() {
        return attribute;
    }

    public V getValue() {
        return value;
    }
}
