package com.kuro.random.jpa.types;

import com.kuro.random.jpa.mapper.HierarchyGraph;
import com.kuro.random.jpa.mapper.TableNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public final class CreationOrder {
    private final HierarchyGraph hierarchyGraph;
    private List<Class<?>> creationPlan;
    private Map<Class<?>, Integer> creationCount;

    private CreationOrder(final HierarchyGraph hierarchyGraph) {
        this.creationPlan = new ArrayList<Class<?>>();
        this.hierarchyGraph = hierarchyGraph;
        creationCount = new HashMap<Class<?>, Integer>();
    }

    public static CreationOrder newInstance(final HierarchyGraph hierarchyGraph) {
        return new CreationOrder(hierarchyGraph);
    }

    public void add(final Class<?> type) {
        creationPlan.add(type);
    }

    public void addCreationCount(final Class<?> type, final int count) {
        creationCount.put(type, count);
    }

    public int getCreationCount(final Class<?> type) {
        final Integer integer = creationCount.get(type);
        return integer == null ? 1 : integer;
    }

    public List<Class<?>> getCreationPlan() {
        return creationPlan;
    }

    public boolean contains(final Class<?> type) {
        return creationPlan.contains(type);
    }

    public TableNode getTableNode(final Class<?> type) {
        return hierarchyGraph.getParentRelations().get(type);
    }
}
