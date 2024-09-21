package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public final class ChildNode {

    private final Class<?> parent;
    private final int level;
    private final Set<Class<?>> childClasses;
    private final Map<Class<?>, Set<Relation>> childRelations;

    public static ChildNode newInstance(final Class<?> parent, final int level) {
        return new ChildNode(parent, level);
    }

    private ChildNode(final Class<?> parent, final int level) {
        this.parent = parent;
        this.childClasses = new HashSet<>();
        this.childRelations = new HashMap<>();
        this.level = level;
    }

    public void addChild(final Class<?> childClass) {
        childClasses.add(childClass);
    }

    public void addRelation(final Relation parentRelation) {

        final FieldWrapper parentRef = parentRelation.getFrom();
        final FieldWrapper childRef = parentRelation.getTo();

        final Relation relation = Relation.newInstance(childRef, parentRef);

        Set<Relation> relations = childRelations.computeIfAbsent(childRef.getInitializationClass(), k -> new HashSet<>());

        relations.add(relation);
    }

    public Set<Class<?>> getChildClasses() {
        return childClasses;
    }

    public Set<Relation> getChildRelations(final Class<?> type) {
        return childRelations.get(type);
    }

}
