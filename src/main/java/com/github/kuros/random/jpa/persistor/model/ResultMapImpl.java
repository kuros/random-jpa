package com.github.kuros.random.jpa.persistor.model;

import com.github.kuros.random.jpa.types.Printer;
import com.github.kuros.random.jpa.types.Node;

import java.util.ArrayList;
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
public final class ResultMapImpl implements ResultMap {

    private final Node root;
    private Map<Class<?>, List<Object>> resultMap;

    private ResultMapImpl(final Node root) {
        this.resultMap = new HashMap<Class<?>, List<Object>>();
        this.root = root;
    }

    public static ResultMapImpl newInstance(final Node root) {
        return new ResultMapImpl(root);
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
