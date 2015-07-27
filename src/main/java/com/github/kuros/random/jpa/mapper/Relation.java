package com.github.kuros.random.jpa.mapper;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;

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
public class Relation {

    private final FieldWrapper from;
    private final FieldWrapper to;

    @VisibleForTesting
    Relation(final FieldWrapper from, final FieldWrapper to) {
        this.from = from;
        this.to = to;
    }

    public static Relation newInstance(final FieldWrapper from, final FieldWrapper to) {
        return new Relation(from, to);
    }

    public FieldWrapper getFrom() {
        return from;
    }

    public FieldWrapper getTo() {
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
