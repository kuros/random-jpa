package com.github.kuros.random.jpa.types;

import com.github.kuros.random.jpa.link.Link;

import jakarta.persistence.metamodel.Attribute;
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
public final class Trigger<T> {

    private Class<T> triggerClass;
    private List<Link> links;

    private Trigger(final Class<T> triggerClass) {
        this.triggerClass = triggerClass;
        this.links = new ArrayList<>();
    }

    public static <T> Trigger<T> of(final Class<T> triggerClass) {
        return new Trigger<>(triggerClass);
    }

    public Trigger withLink(final Attribute<T, ?> triggerAttribute, final Attribute dependsOnAttribute) {
        links.add(Link.newLink(triggerAttribute, dependsOnAttribute));
        return this;
    }

    public Class<T> getTriggerClass() {
        return triggerClass;
    }

    public List<Link> getLinks() {
        return links;
    }
}
