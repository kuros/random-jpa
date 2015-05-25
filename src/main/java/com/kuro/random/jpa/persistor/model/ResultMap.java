package com.kuro.random.jpa.persistor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public final class ResultMap {

    private Map<Class<?>, List<Object>> resultMap;

    private ResultMap() {
        this.resultMap = new HashMap<Class<?>, List<Object>>();
    }

    public static ResultMap newInstance() {
        return new ResultMap();
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

}
