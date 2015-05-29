package com.github.kuros.random.jpa.types;

import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.persistor.model.ResultMapImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/25/15.
 */
public class CreationPlanImpl implements CreationPlan {
    private CreationOrder creationOrder;
    private ResultMap resultMap;
    private Map<Class<?>, List<Node>> createdNodeMap;
    private Node root;

    public CreationPlanImpl(final CreationOrder creationOrder) {
        this.createdNodeMap = new HashMap<Class<?>, List<Node>>();
        this.root = Node.newInstance();
        this.resultMap = ResultMapImpl.newInstance(root);
        this.creationOrder = creationOrder;
    }

    @Override
    public Map<Class<?>, List<Node>> getCreatedNodeMap() {
        return createdNodeMap;
    }

    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        return (T) createdNodeMap.get(type).get(0).getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type, final int index) {
        return (T) createdNodeMap.get(type).get(index).getValue();
    }

    @Override
    public void print(final Printer printer) {
        root.print(printer);
    }

    @Override
    public CreationOrder getCreationOrder() {
        return creationOrder;
    }

    @Override
    public <T> void deleteItem(final Class<T> type, final int index) {
        final Node node = createdNodeMap.get(type).get(index);
        node.setValue(null);
        node.setChildNodes(new ArrayList<Node>());
    }
}
