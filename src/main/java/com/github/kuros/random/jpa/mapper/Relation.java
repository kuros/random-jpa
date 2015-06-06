package com.github.kuros.random.jpa.mapper;

import java.lang.reflect.Field;

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
public final class Relation {

    private final Field from;
    private final Field to;

    private Relation(final Field from, final Field to) {
        this.from = from;
        this.to = to;
    }

    public static Relation newInstance(final Field from, final Field to) {
        return new Relation(from, to);
    }

    public Field getFrom() {
        return from;
    }

    public Field getTo() {
        return to;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final Relation relation = (Relation) o;

        return from.equals(relation.from) && to.equals(relation.to);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }
}
