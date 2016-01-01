package com.github.kuros.random.jpa.types;

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
public final class CreationOrder {
    private List<Class<?>> order;
    private Map<Class<?>, Integer> creationCount;

    private CreationOrder() {
        this.order = new ArrayList<Class<?>>();
        this.creationCount = new HashMap<Class<?>, Integer>();
    }

    public static CreationOrder newInstance() {
        return new CreationOrder();
    }

    public void add(final Class<?> type) {
        order.add(type);
    }

    public void addCreationCount(final Class<?> type, final int count) {
        creationCount.put(type, count);
    }

    public Map<Class<?>, Integer> getCreationCount() {
        return creationCount;
    }

    public List<Class<?>> getOrder() {
        return order;
    }

    public boolean contains(final Class<?> type) {
        return order.contains(type);
    }
}
