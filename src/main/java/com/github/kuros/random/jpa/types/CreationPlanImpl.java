package com.github.kuros.random.jpa.types;

import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.util.AttributeHelper;

import javax.persistence.metamodel.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class CreationPlanImpl implements CreationPlan {
    private Randomize randomize;
    private Map<Class<?>, List<Node>> createdNodeMap;
    private List<FieldIndexValue> fieldIndexValues;
    private Node root;
    private final HierarchyGraph hierarchyGraph;

    public CreationPlanImpl(final HierarchyGraph hierarchyGraph, final Randomize randomize) {
        this.createdNodeMap = new HashMap<Class<?>, List<Node>>();
        this.root = Node.newInstance();
        this.randomize = randomize;
        this.hierarchyGraph = hierarchyGraph;
        this.fieldIndexValues = new ArrayList<FieldIndexValue>();
    }

    public Map<Class<?>, List<Node>> getCreatedNodeMap() {
        return createdNodeMap;
    }

    public Node getRoot() {
        return root;
    }

    public <T, V> void set(final Attribute<T, V> attribute, final V value) {
        set(0, attribute, value);
    }

    @SuppressWarnings("unchecked")
    public <T, V> void set(final int index, final Attribute<T, V> attribute, final V value) {
        fieldIndexValues.add(new FieldIndexValue(AttributeHelper.getField(attribute), index, value));
    }

    public void print(final Printer printer) {
        printer.print(root.print());
    }

    @SuppressWarnings("unchecked")
    public <T> void deleteItem(final Class<T> type, final int index) {
        final Node node = createdNodeMap.get(type).get(index);
        node.setValue(null);
        node.setChildNodes(new ArrayList<Node>());
    }

    public Randomize getRandomize() {
        return randomize;
    }

    public HierarchyGraph getHierarchyGraph() {
        return hierarchyGraph;
    }

    public List<FieldIndexValue> getFieldIndexValues() {
        return fieldIndexValues;
    }
}
