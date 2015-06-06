package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;

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
public final class HierarchyGraph {

    private Map<Class<?>, TableNode> parentRelations;
    private Map<Class<?>, Set<Relation>> attributeRelations;

    private HierarchyGraph() {
        this.parentRelations = new HashMap<Class<?>, TableNode>();
        attributeRelations = new HashMap<Class<?>, Set<Relation>>();
    }

    public static HierarchyGraph newInstance() {
        return new HierarchyGraph();
    }

    public void addRelation(final Relation relation) {

        final Class<?> fromClass = relation.getFrom().getDeclaringClass();
        TableNode tableNode = parentRelations.get(fromClass);
        if (tableNode == null) {
            tableNode = TableNode.newInstance();
        }

        tableNode.addRelation(relation);
        final Class<?> toClass = relation.getTo().getDeclaringClass();
        tableNode.addParent(toClass);
        parentRelations.put(fromClass, tableNode);

        if (!parentRelations.containsKey(toClass)) {
            parentRelations.put(toClass, TableNode.newInstance());
        }

        populateAttributeRelations(fromClass, relation);
    }

    private void populateAttributeRelations(final Class<?> fromClass, final Relation relation) {
        Set<Relation> relations = attributeRelations.get(fromClass);
        if (relations == null) {
            relations = new HashSet<Relation>();
            attributeRelations.put(fromClass, relations);
        }

        relations.add(relation);
    }

    public Set<Class<?>> getKeySet() {
        return parentRelations.keySet();
    }

    public Set<Class<?>> getParents(final Class tableClass) {
        final TableNode tableNode = parentRelations.get(tableClass);
        return tableNode != null ? tableNode.getParentClasses() : new HashSet<Class<?>>();
    }

    public TableNode getTableNode(final Class tableClass) {
        return parentRelations.get(tableClass);
    }

    public Map<Class<?>, TableNode> getParentRelations() {
        return parentRelations;
    }

    public Set<Relation> getAttributeRelations(final Class<?> tableClass) {
        return attributeRelations.get(tableClass);
    }
}
