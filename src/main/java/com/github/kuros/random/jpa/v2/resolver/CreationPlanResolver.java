package com.github.kuros.random.jpa.v2.resolver;

import com.github.kuros.random.jpa.definition.ChildNode;
import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.types.CreationGraph;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.CreationPlanImpl;
import com.github.kuros.random.jpa.types.Node;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class CreationPlanResolver {

    private CreationGraph creationGraph;
    private CreationPlan creationPlan;
    private Randomize randomize;

    private CreationPlanResolver(final CreationGraph creationGraph, final Randomize randomize) {
        this.creationGraph = creationGraph;
        this.randomize = randomize;
    }

    public static CreationPlanResolver newInstance(final CreationGraph creationGraph, final Randomize randomize) {
        return new CreationPlanResolver(creationGraph, randomize);
    }

    public CreationPlan create() {
        creationPlan = new CreationPlanImpl(randomize);
        resolve();
        return creationPlan;
    }

    private void resolve() {
        final List<ChildNode> parentNodes = creationGraph.getParentNodes();
        if (parentNodes.isEmpty()) {
            return;
        }

        final PriorityQueue<ChildNode> priorityQueue = new PriorityQueue<ChildNode>(new Comparator<ChildNode>() {
            public int compare(final ChildNode o1, final ChildNode o2) {
                return -1 * o1.getLevel().compareTo(o2.getLevel());
            }
        });

        for (ChildNode parentNode : parentNodes) {
            priorityQueue.offer(parentNode);


        }
    }

    private void add(final Node node, final int index) {

    }
}
