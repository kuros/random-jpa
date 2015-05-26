package com.kuro.random.jpa.types;

import com.kuro.random.jpa.persistor.model.ResultMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/25/15.
 */
public class CreationPlan {
    private CreationOrder creationOrder;
    private ResultMap resultMap;
    private Map<Class<?>, List<Node>> createdNodeMap;
    private Node root;

    public CreationPlan(final CreationOrder creationOrder) {
        this.createdNodeMap = new HashMap<Class<?>, List<Node>>();
        this.root = Node.newInstance();
        this.resultMap = ResultMap.newInstance(root);
        this.creationOrder = creationOrder;
    }

    public Map<Class<?>, List<Node>> getCreatedNodeMap() {
        return createdNodeMap;
    }

    public Node getRoot() {
        return root;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        return (T) createdNodeMap.get(type).get(0).getValue();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type, final int index) {
        return (T) createdNodeMap.get(type).get(index).getValue();
    }

    public void print(final Printer printer) {
        root.print(printer);
    }

    public CreationOrder getCreationOrder() {
        return creationOrder;
    }

    public <T> void deleteItem(final Class<T> type, final int index) {
        final Node node = createdNodeMap.get(type).get(index);
        node.setValue(null);
        node.setChildNodes(new ArrayList<Node>());
    }
}
