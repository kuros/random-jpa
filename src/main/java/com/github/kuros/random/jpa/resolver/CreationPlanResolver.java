package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.mapper.TableNode;
import com.github.kuros.random.jpa.mapper.FieldValue;
import com.github.kuros.random.jpa.persistor.random.Randomize;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Node;
import com.github.kuros.random.jpa.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/25/15.
 */
public final class CreationPlanResolver {

    private CreationOrder creationOrder;
    private List<Class<?>> order;
    private Map<Class<?>, Integer> creationCount;
    private CreationPlan creationPlan;
    private Randomize randomize;

    private CreationPlanResolver(final CreationOrder creationOrder, final Randomize randomize) {
        this.creationOrder = creationOrder;
        this.order = creationOrder.getOrder();
        this.creationCount = creationOrder.getCreationCount();
        this.randomize = randomize;
    }

    public static CreationPlanResolver newInstance(final CreationOrder creationOrder, final Randomize randomize) {
        return new CreationPlanResolver(creationOrder, randomize);
    }

    public CreationPlan create() {
        creationPlan = new CreationPlan(creationOrder);

        add(creationPlan.getRoot(), 0);

        return creationPlan;
    }

    private void add(final Node node, final int index) {
        if (index >= order.size()) {
            return;
        }

        final Class<?> type = order.get(index);
        Integer count = creationCount.get(type);
        count = count == null ? 1 : count;

        for (int i = 0; i < count; i++) {
            final Node childNode = Node.newInstance(type, getCreatedIndex(type));

            final Object randomObject = createRandomObject(childNode);
            childNode.setValue(randomObject);
            node.addChildNode(childNode);
            addToCreatedNode(type, childNode);
            add(childNode, index + 1);
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
        final Class<?> type = node.getType();
        final Object random = randomize.createRandom(type);

        final TableNode tableNode = creationOrder.getTableNode(type);
        final List<Relation<?, ?>> relations = tableNode.getRelations();

        for (Relation relation : relations) {
            createRelation(relation, random);
        }


        return random;
    }

    private <F, T> void createRelation(final Relation<F, T> relation, final Object object) {
        try {
            final Object value = getFieldValue(relation.getTo());
            setFieldValue(object, relation.getFrom(), value);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    private <F> void setFieldValue(final Object object, final FieldValue<F> fieldValue, final Object value) {
        try {

            final Class<?> type = fieldValue.getField().getType();
            fieldValue.getField().setAccessible(true);
            fieldValue.getField().set(object, NumberUtil.castNumber(type, value));
        } catch (final IllegalAccessException e) {
        }
    }



    private <T> Object getFieldValue(final FieldValue<T> fieldValue) {
        final List<Node> nodes = creationPlan.getCreatedNodeMap().get(fieldValue.getField().getDeclaringClass());
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        final Object object = nodes.get(nodes.size() - 1).getValue();
        Object value = null;
        try {
            fieldValue.getField().setAccessible(true);
            value = fieldValue.getField().get(object);
        } catch (final IllegalAccessException e) {
        }
        return value;
    }

}
