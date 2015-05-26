package com.github.incrediblevicky.random.jpa.mapper;

import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/3/15.
 */
public final class FieldValue<V> {

    private final Field field;
    private V value;

    public static <V> FieldValue<V> newInstance(final Field field) {
        return new FieldValue<V>(field, null);
    }

    public static <V> FieldValue<V> newInstance(final Field field, final V value) {
        return new FieldValue<V>(field, value);
    }

    private FieldValue(final Field field, final V value) {
        this.field = field;
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public V getValue() {
        return value;
    }

    public void setValue(final V value) {
        this.value = value;
    }
}
