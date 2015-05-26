package com.github.incrediblevicky.random.jpa.persistor.model;

import com.github.incrediblevicky.random.jpa.types.Printer;
import com.github.incrediblevicky.random.jpa.types.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public final class ResultMap {

    private final Node root;
    private Map<Class<?>, List<Object>> resultMap;

    private ResultMap(final Node root) {
        this.resultMap = new HashMap<Class<?>, List<Object>>();
        this.root = root;
    }

    public static ResultMap newInstance(final Node root) {
        return new ResultMap(root);
    }

    public void put(final Class<?> type, final Object object) {
        List<Object> objects = resultMap.get(type);
        if (objects == null) {
            objects = new ArrayList<Object>();
            resultMap.put(type, objects);
        }
        objects.add(object);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        return get(type, 0);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type, final int index) {
        final List<?> objects = resultMap.get(type);
        return objects == null ? null : (T) objects.get(index);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(final Class<T> type) {
        return (List<T>) resultMap.get(type);
    }

    public Map<Class<?>, List<Object>> getCreatedEntities() {
        return resultMap;
    }

    public void print(final Printer printer) {
        root.print(printer);
    }
}
