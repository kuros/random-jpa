package com.github.kuros.random.jpa.v1.resolver;

import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.CreationPlanImpl;
import com.github.kuros.random.jpa.types.Node;

import java.util.ArrayList;
import java.util.Arrays;
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
public final class CreationPlanResolver {

    private List<CreationOrder> creationOrders;
    private Map<Class<?>, Integer> creationCount;
    private CreationPlan creationPlan;
    private Randomize randomize;

    private CreationPlanResolver(final Randomize randomize, final CreationOrder... creationOrders) {
        this.creationOrders = Arrays.asList(creationOrders);
        this.creationCount = new HashMap<Class<?>, Integer>();
        this.randomize = randomize;

        initCreationCount();
    }

    private void initCreationCount() {
        for (CreationOrder order : creationOrders) {
            creationCount.putAll(order.getCreationCount());
        }
    }

    public static CreationPlanResolver newInstance(final Randomize randomize, final CreationOrder... creationOrders) {
        return new CreationPlanResolver(randomize, creationOrders);
    }

    public CreationPlan create() {
        creationPlan = new CreationPlanImpl(randomize);

        for (CreationOrder creationOrder : creationOrders) {
            final List<ClassDepth<?>> order = creationOrder.getOrder();
            add(order, creationPlan.getRoot(), 0);
        }

        return creationPlan;
    }

    @SuppressWarnings("unchecked")
    private void add(final List<ClassDepth<?>> order, final Node node, final int index) {
        if (index >= order.size()) {
            return;
        }

        final ClassDepth<?> classDepth = order.get(index);
        final Class<?> type = classDepth.getType();
        Integer count = creationCount.get(type);
        count = count == null ? 1 : count;

        for (int i = 0; i < count; i++) {
            final Node childNode = Node.newInstance(type, classDepth.getDepth(), getCreatedIndex(type));

            final Object randomObject = createRandomObject(childNode);
            childNode.setValue(randomObject);
            node.addChildNode(childNode);
            addToCreatedNode(type, childNode);
            add(order, childNode, index + 1);
        }
    }

    private void addToCreatedNode(final Class<?> key, final Node node) {
        List<Node> nodes = creationPlan.getCreatedNodeMap().get(key);
        if (nodes == null) {
            nodes = new ArrayList<Node>();
            creationPlan.getCreatedNodeMap().put(key, nodes);
        }

        nodes.add(node);
    }

    private int getCreatedIndex(final Class<?> key) {
        final List<Node> nodes = creationPlan.getCreatedNodeMap().get(key);
        return nodes == null ? 0 : nodes.size();
    }

    private Object createRandomObject(final Node node) {
        return randomize.createRandom(node.getType());
    }
}
