package com.kuro.random.jpa.persistor.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public final class ResultMap {

    private Map<Class<?>, Object> resultMap;

    private ResultMap() {
        this.resultMap = new HashMap<Class<?>, Object>();
    }

    public static ResultMap newInstance() {
        return new ResultMap();
    }

    public <T> void put(final Class<T> type, final T t) {
        resultMap.put(type, t);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        return (T) resultMap.get(type);
    }
}
