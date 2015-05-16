package com.kuro.random.jpa.types;

import com.kuro.random.jpa.mapper.HierarchyGraph;
import com.kuro.random.jpa.mapper.TableNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public final class CreationPlan {
    private final HierarchyGraph hierarchyGraph;
    private List<Class<?>> creationPlan;

    private CreationPlan(final HierarchyGraph hierarchyGraph) {
        this.creationPlan = new ArrayList<Class<?>>();
        this.hierarchyGraph = hierarchyGraph;
    }

    public static CreationPlan newInstance(final HierarchyGraph hierarchyGraph) {
        return new CreationPlan(hierarchyGraph);
    }

    public void add(final Class<?> type) {
        creationPlan.add(type);
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
