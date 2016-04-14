package com.github.kuros.random.jpa.types;

import java.lang.reflect.Field;

public class FieldIndex {

    private Field field;
    private int index;

    public FieldIndex(final Field field, final int index) {
        this.field = field;
        this.index = index;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FieldIndex that = (FieldIndex) o;

        if (index != that.index) return false;
        return field != null ? field.equals(that.field) : that.field == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }
}
