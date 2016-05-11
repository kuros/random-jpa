package com.github.kuros.random.jpa.types;

public final class ClassIndex {

    private Class<?> type;
    private int index;

    private ClassIndex(final Class<?> type, final Integer index) {
        this.type = type;
        this.index = index == null ? 0 : index;
    }

    public static ClassIndex newInstance(final Class<?> type, final Integer index) {
        return new ClassIndex(type, index);
    }

    public Class<?> getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ClassIndex that = (ClassIndex) o;

        return index == that.index && (type != null ? type.equals(that.type) : that.type == null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return type.getName() + " : " + index;
    }
}
