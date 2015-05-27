package com.github.kuros.random.jpa.link;

import javax.persistence.metamodel.Attribute;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public final class Link {

    private Attribute from;
    private Attribute to;

    private Link(final Attribute from, final Attribute to) {
        this.from = from;
        this.to = to;
    }

    public static Link newLink(final Attribute from, final Attribute to) {
        return new Link(from, to);
    }

    public Attribute getFrom() {
        return from;
    }

    public Attribute getTo() {
        return to;
    }
}
