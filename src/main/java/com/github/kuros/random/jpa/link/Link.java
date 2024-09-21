package com.github.kuros.random.jpa.link;

import jakarta.persistence.metamodel.Attribute;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
