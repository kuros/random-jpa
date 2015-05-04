package com.kuro.random.jpa.mapper;

import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/3/15.
 */
public class FieldValue<V> {

    private final Field field;
    private final V value;

    public static <V> FieldValue<V> newIntance(final Field field) {
        return new FieldValue<V>(field, null);
    }

    public static <V> FieldValue<V> newIntance(final Field field, final V value) {
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


}
