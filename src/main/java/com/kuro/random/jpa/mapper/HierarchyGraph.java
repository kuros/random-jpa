package com.kuro.random.jpa.mapper;

import com.kuro.random.jpa.util.FieldValueHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/1/15.
 */
public final class HierarchyGraph {

    private Map<Class<?>, TableNode> parentRelations;

    private HierarchyGraph() {
        this.parentRelations = new HashMap<Class<?>, TableNode>();
    }

    public static HierarchyGraph newInstance() {
        return new HierarchyGraph();
    }

    public void addRelation(final Relation relation) {

        System.out.println(relation.getFrom().getField() + " --> " + relation.getTo().getField());

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

//        final TableNode toNode = FieldValueHelper.getTableNode(relation.getTo());
//        if (!parentNodes.contains(toNode)) {
//            parentNodes.add(toNode);
//        }
//
//        final TableNode fromTableNode = FieldValueHelper.getTableNode(relation.getFrom());
//        fromTableNode.addRelation(relation);
//        parentRelations.put(fromTableNode, parentNodes);
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


}
