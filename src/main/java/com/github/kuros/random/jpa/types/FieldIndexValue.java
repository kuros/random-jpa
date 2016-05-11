package com.github.kuros.random.jpa.types;

import java.lang.reflect.Field;

public class FieldIndexValue<V> {

    private final Field field;
    private final int index;
    private final V value;

    public FieldIndexValue(final Field field, final int index, final V value) {
        this.field = field;
        this.index = index;
        this.value = value;
    }

    public FieldIndexValue(final Field field, final V value) {
        this(field, -1, value);
    }

    public Field getField() {
        return field;
    }

    public Integer getIndex() {
        return index;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FieldIndexValue<?> that = (FieldIndexValue<?>) o;

        if (index != that.index) return false;
        return field.equals(that.field);

    }

    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + index;
        return result;
    }
}
