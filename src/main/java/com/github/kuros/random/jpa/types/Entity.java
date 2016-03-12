package com.github.kuros.random.jpa.types;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.link.Link;

import javax.persistence.metamodel.Attribute;
import java.util.ArrayList;
import java.util.List;

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
public final class Entity<T> {

    private Class<T> type;
    private List<AttributeValue> attributeValues;
    private List<Link> softLinks;
    private int count;

    private Entity(final Class<T> type) {
        this(type, 1);
    }

    private Entity(final Class<T> type, final int count) {
        validate(count);
        this.type = type;
        this.attributeValues = new ArrayList<AttributeValue>();
        this.count = count;
        this.softLinks = new ArrayList<Link>();
    }

    private void validate(final int entityCount) {
        if (entityCount < 1) {
            throw new RandomJPAException("Illegal Argument: count should be greater than 0");
        }
    }

    public static <T> Entity<T> of(final Class<T> type) {
        return new Entity<T>(type);
    }

    public static <T> Entity<T> of(final Class<T> type, final int count) {
        return new Entity<T>(type, count);
    }

    public <V> Entity<T> with(final Attribute<T, V> attribute, final V value) {
        attributeValues.add(AttributeValue.newInstance(attribute, value));
        return this;
    }

    public <V> Entity<T> withSoftLink(final Attribute<T, V> attribute, final Attribute<?, V> linksTo) {
        softLinks.add(Link.newLink(attribute, linksTo));
        return this;
    }

    public Class<T> getType() {
        return type;
    }

    public List<AttributeValue> getAttributeValues() {
        return attributeValues;
    }

    public int getCount() {
        return count;
    }

    public List<Link> getSoftLinks() {
        return softLinks;
    }
}
