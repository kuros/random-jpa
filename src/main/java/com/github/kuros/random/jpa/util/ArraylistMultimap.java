package com.github.kuros.random.jpa.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public final class ArrayListMultimap<T, V> implements Multimap<T, V> {
    private Map<T, List<V>> map;

    private ArrayListMultimap() {
        this.map = new HashMap<T, List<V>>();
    }

    public static <T, V> ArrayListMultimap<T, V> newArrayListMultimap() {
        return new ArrayListMultimap<T, V>();
    }

    public void put(final T t, final V v) {
        List<V> list = map.get(t);
        if (list == null) {
            list = new ArrayList<V>();
            map.put(t, list);
        }

        list.add(v);
    }

    public List<V> get(final T t) {
        return map.get(t);
    }

    public Set<T> getKeySet() {
        return map.keySet();
    }
}
