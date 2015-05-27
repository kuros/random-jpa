package com.github.kuros.random.jpa.types;

import javax.persistence.metamodel.Attribute;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/17/15.
 */
public final class Entity<T> {

    private Class<T> type;
    private List<AttributeValue> attributeValues;
    private int count;

    private Entity(final Class<T> type) {
        this(type, 1);
    }

    private Entity(final Class<T> type, final int count) {
        this.type = type;
        this.attributeValues = new ArrayList<AttributeValue>();
        this.count = count;
    }

    public static <T> Entity<T> of(final Class<T> type) {
        return new Entity<T>(type);
    }

    public static <T> Entity<T> of(final Class<T> type, final int count) {
        return new Entity<T>(type, count);
    }

    public <V> Entity<T> with(final Attribute<T, V> attribute, final V value) {
        attributeValues.add(AttributeValue.newInstance(attribute, value));
        return this;
    }

    public Class<T> getType() {
        return type;
    }

    public List<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public int getCount() {
        return count;
    }
}
