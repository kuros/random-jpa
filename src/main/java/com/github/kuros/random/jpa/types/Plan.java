package com.github.kuros.random.jpa.types;

import com.github.kuros.random.jpa.link.Before;
import com.github.kuros.random.jpa.link.Preconditions;

import javax.persistence.metamodel.Attribute;
import java.util.ArrayList;
import java.util.Collections;
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
public final class Plan {

    private List<Entity> entities;
    private List<Attribute<?, ?>> nullValueAttributes;
    private Preconditions preconditions;

    private Plan() {
        this.entities = new ArrayList<Entity>();
        this.nullValueAttributes = new ArrayList<Attribute<?, ?>>();
        this.preconditions = new Preconditions();
    }

    public static Plan create() {
        return new Plan();
    }

    public static Plan of(final Entity<?>... entities) {
        final Plan plan = new Plan();
        for (Entity<?> entity : entities) {
            plan.add(entity);
        }

        return plan;
    }

    public <T> Plan add(final Entity<T> entity) {
        entities.add(entity);
        return this;
    }

    @Deprecated
    public <T, V> Plan withNullValues(final Attribute<T, V>... attributes) {
        Collections.addAll(nullValueAttributes, attributes);
        return this;
    }

    @Deprecated
    public Plan withPreconditons(final Before... befores) {
        for (Before before : befores) {
            preconditions.add(before.getType(), createPlan(before.getToClasses()));
        }

        return this;
    }

    private Plan createPlan(final List<Class<?>> collection) {
        final Plan plan = Plan.of();
        for (Class<?> aClass : collection) {
            plan.add(Entity.of(aClass));
        }

        return plan;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Attribute<?, ?>> getNullValueAttributes() {
        return nullValueAttributes;
    }

    public Preconditions getPreconditions() {
        return preconditions;
    }
}
