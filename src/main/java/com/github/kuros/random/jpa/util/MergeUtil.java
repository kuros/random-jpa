package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MergeUtil {

    public static List<CreationOrder> merge(final List<CreationOrder> creationOrders) {
        final Map<OrderKey, CreationOrder> compactMap = new LinkedHashMap<OrderKey, CreationOrder>();
        final Map<OrderKey, CreationOrder> map = new LinkedHashMap<OrderKey, CreationOrder>();

        for (CreationOrder creationOrder : creationOrders) {

            final CreationOrder combinedOrder = copy(creationOrder);

            for (ClassDepth<?> classDepth : creationOrder.getOrder()) {
                final OrderKey orderKey = new OrderKey(classDepth);
                final CreationOrder innerOrder = map.get(orderKey);
                if (innerOrder != null && !innerOrder.getOrder().isEmpty()) {
                    compactMap.remove(orderKey);
                    mergeRightToLeft(combinedOrder, innerOrder);
                }
            }

            if (!combinedOrder.getOrder().isEmpty()) {
                final OrderKey key = new OrderKey(combinedOrder.getOrder());
                compactMap.put(key, combinedOrder);
                map.put(key, combinedOrder);
            }
        }

        return new ArrayList<CreationOrder>(compactMap.values());
    }

    private static void mergeRightToLeft(final CreationOrder left, final CreationOrder right) {
        final List<ClassDepth<?>> leftOrder = left.getOrder();
        for (ClassDepth<?> rightOrderClass : right.getOrder()) {
            if (!leftOrder.contains(rightOrderClass)) {
                left.add(rightOrderClass);
            } else {
                final int index = leftOrder.indexOf(rightOrderClass);
                final ClassDepth<?> leftClassDepth = leftOrder.get(index);
                if (leftClassDepth.getDepth() < rightOrderClass.getDepth()) {
                    leftClassDepth.setDepth(rightOrderClass.getDepth());
                }
            }
        }

        left.addCreationCount(right.getCreationCount());
    }

    private static CreationOrder copy(final CreationOrder in) {
        final CreationOrder out = CreationOrder.newInstance();

        for (ClassDepth<?> classDepth : in.getOrder()) {
            out.add(classDepth);
        }

        out.addCreationCount(in.getCreationCount());

        return out;
    }

    private static class OrderKey {

        private List<ClassDepth<?>> classes;

        OrderKey(final List<ClassDepth<?>> classes) {
            this.classes = classes;
        }

        OrderKey(final ClassDepth<?> aClass) {
            this.classes = new ArrayList<ClassDepth<?>>();
            this.classes.add(aClass);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final OrderKey orderKey = (OrderKey) o;

            for (ClassDepth<?> aClass : orderKey.getClasses()) {
                if (classes.contains(aClass)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public int hashCode() {
            return 21;
        }

        public List<ClassDepth<?>> getClasses() {
            return classes;
        }
    }
}
