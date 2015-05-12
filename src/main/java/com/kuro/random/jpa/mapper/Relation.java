package com.kuro.random.jpa.mapper;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public class Relation<F, T> {

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
}
