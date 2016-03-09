package com.github.kuros.random.jpa.types;

public final class ClassDepth<T> {

    private Class<T> type;
    private int depth;

    private ClassDepth(final Class<T> type, final int depth) {
        this.type = type;
        this.depth = depth;
    }

    @SuppressWarnings("unchecked")
    public static <T> ClassDepth newInstance(final Class<T> type, final int depth) {
        return new ClassDepth(type, depth);
    }

    public Class<T> getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(final int depth) {
        this.depth = depth;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ClassDepth<?> that = (ClassDepth<?>) o;

        return type.equals(that.type);

    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
