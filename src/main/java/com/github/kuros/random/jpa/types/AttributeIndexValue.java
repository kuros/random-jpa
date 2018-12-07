package com.github.kuros.random.jpa.types;

import javax.persistence.metamodel.Attribute;

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
public final class AttributeIndexValue<T, V> {

    private final Attribute<T, V> attribute;
    private final int index;
    private final V value;

    private AttributeIndexValue(final Attribute<T, V> attribute, final int index, final V value) {
        this.attribute = attribute;
        this.value = value;
        this.index = index;
    }

    public static <T, V> AttributeIndexValue<T, V> newInstance(final Attribute<T, V> attribute, final int index, final V value) {
        return new AttributeIndexValue<>(attribute, index, value);
    }

    public Attribute<T, V> getAttribute() {
        return attribute;
    }

    public V getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }
}
