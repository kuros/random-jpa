package com.kuro.random.jpa.mapper;

import com.kuro.random.jpa.util.FieldValueHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/1/15.
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

        final Class<?> fromClass = FieldValueHelper.getDeclaringClass(relation.getFrom());
        TableNode tableNode = parentRelations.get(fromClass);
        if (tableNode == null) {
            tableNode = TableNode.newInstance();
        }

        tableNode.addRelation(relation);
        final Class<?> toClass = FieldValueHelper.getDeclaringClass(relation.getTo());
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
