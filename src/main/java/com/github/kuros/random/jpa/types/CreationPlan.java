package com.github.kuros.random.jpa.types;

import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/29/15.
 */
public interface CreationPlan {
    Map<Class<?>, List<Node>> getCreatedNodeMap();

    Node getRoot();

    @SuppressWarnings("unchecked")
    <T> T get(Class<T> type);

    @SuppressWarnings("unchecked")
    <T> T get(Class<T> type, int index);

    void print(Printer printer);

    CreationOrder getCreationOrder();

    <T> void deleteItem(Class<T> type, int index);
}
