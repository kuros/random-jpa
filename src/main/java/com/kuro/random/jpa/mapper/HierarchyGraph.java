package com.kuro.random.jpa.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/1/15.
 */
public class HierarchyGraph {

    private Map<TableNode, List<TableNode>> parentRelations;

    private HierarchyGraph() {
        this.parentRelations = new HashMap<TableNode, List<TableNode>>();
    }

    public static HierarchyGraph newInstance() {
        return new HierarchyGraph();
    }

    public void addRelation(final FieldValue from, final FieldValue to) {
        List<TableNode> fromNodes = parentRelations.get(getTableNode(from));
        if (fromNodes == null) {
            fromNodes = new ArrayList<TableNode>();
            parentRelations.put(getTableNode(from), fromNodes);
        }

        final TableNode<?> toNode = getTableNode(to);

        if (fromNodes.contains(toNode)) {
            final TableNode<?> tableNode = fromNodes.get(fromNodes.indexOf(toNode));
            tableNode.addAttributes(to);
        } else {
            fromNodes.add(toNode);
        }
    }

    public Set<TableNode> getKeySet() {
        return parentRelations.keySet();
    }

    public List<TableNode> getParent(final Class tableClass) {
        final TableNode tableNode = TableNode.newInstance(tableClass);
        return parentRelations.get(tableNode);
    }

    public Map<TableNode, List<TableNode>> getParentRelations() {
        return parentRelations;
    }

    private TableNode getTableNode(final FieldValue fieldValue) {
        final Field field = fieldValue.getField();
        final TableNode tableNode = TableNode.newInstance(field.getDeclaringClass());
        tableNode.addAttributes(fieldValue);
        return tableNode;
    }
}
