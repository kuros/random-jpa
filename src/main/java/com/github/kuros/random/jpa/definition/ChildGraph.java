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
public final class ChildGraph {

    private Map<Class<?>, ChildNode> childNodes;

    private ChildGraph() {
        childNodes = new HashMap<Class<?>, ChildNode>();
    }

    private ChildGraph(final HierarchyGraph hierarchyGraph) {
        childNodes = new HashMap<Class<?>, ChildNode>();

        final Set<Class<?>> childs = hierarchyGraph.getKeySet();

        for (Class<?> child : childs) {
            final Set<Class<?>> parents = hierarchyGraph.getParents(child);
            final Set<Relation> attributeRelations = hierarchyGraph.getAttributeRelations(child);
            for (Class<?> parent : parents) {
                addRelation(getChildNode(parent, 0), child, attributeRelations);
            }
        }
    }

    public static ChildGraph newInstance() {
        return new ChildGraph();
    }

    public static ChildGraph newInstance(final HierarchyGraph hierarchyGraph) {
        return new ChildGraph(hierarchyGraph);
    }

    public void addRelation(final ChildNode childNode, final Class<?> child, final Set<Relation> attributeRelations) {
        childNode.addChild(child);
        if (attributeRelations != null) {
            for (Relation attributeRelation : attributeRelations) {
                childNode.addRelation(attributeRelation);
            }
        }
    }

    public ChildNode getChildNode(final Class<?> parent, final int level) {
        ChildNode childNode = childNodes.get(parent);
        if (childNode == null) {
            childNode = ChildNode.newInstance(level);
            childNodes.put(parent, childNode);
        }
        return childNode;
    }

    public Set<Class<?>> getChilds(final Class<?> type) {
        final ChildNode childNode = childNodes.get(type);
        return childNode == null ? new HashSet<Class<?>>() : childNode.getChildClasses();
    }

    public Set<Relation> getChildRelations(final Class<?> type) {
        final ChildNode childNode = childNodes.get(type);

        if (childNode == null) {
            return new HashSet<Relation>();
        }

        final Set<Relation> childRelations = childNode.getChildRelations(type);
        return childRelations == null ? new HashSet<Relation>() : childRelations;
    }

    public Set<Class<?>> keySet() {
        return childNodes.keySet();
    }
}
