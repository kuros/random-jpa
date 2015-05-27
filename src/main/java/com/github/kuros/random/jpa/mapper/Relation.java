package com.github.kuros.random.jpa.mapper;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public final class Relation<F, T> {

    private final FieldValue<F> from;
    private final FieldValue<T> to;

    private Relation(final FieldValue<F> from, final FieldValue<T> to) {
        this.from = from;
        this.to = to;
    }

    public static <F, T> Relation<F, T> newInstance(final FieldValue<F> from, final FieldValue<T> to) {
        return new Relation<F, T>(from, to);
    }

    public FieldValue<F> getFrom() {
        return from;
    }

    public FieldValue<T> getTo() {
        return to;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Relation<?, ?> relation = (Relation<?, ?>) o;

        if (!from.getField().equals(relation.from.getField())) return false;
        return to.getField().equals(relation.to.getField());

    }

    @Override
    public int hashCode() {
        int result = from.getField().hashCode();
        result = 31 * result + to.getField().hashCode();
        return result;
    }
}
