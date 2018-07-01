package com.github.kuros.random.jpa.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
public final class CreationOrder {
    private Set<Class<?>> managedClasses;
    private List<ClassDepth<?>> order;
    private Map<Class<?>, Integer> creationCount;

    private CreationOrder() {
        this.order = new ArrayList<>();
        this.creationCount = new HashMap<>();
        this.managedClasses = new HashSet<>();
    }

    public static CreationOrder newInstance() {
        return new CreationOrder();
    }

    public void add(final ClassDepth<?> type) {
        order.add(type);
        managedClasses.add(type.getType());
    }

    public void addCreationCount(final Class<?> type, final int count) {
        creationCount.put(type, count);
    }

    public void addCreationCount(final Map<Class<?>, Integer> creationCountValue) {

        creationCount.putAll(creationCountValue);
    }

    public Map<Class<?>, Integer> getCreationCount() {
        return creationCount;
    }

    public List<ClassDepth<?>> getOrder() {
        return order;
    }

    public boolean contains(final ClassDepth<?> type) {
        return order.contains(type);
    }

    public boolean containsClass(final Class<?> type) {
        return managedClasses.contains(type);
    }

    public void setOrder(final List<ClassDepth<?>> order) {
        this.order = order;
        this.managedClasses = getClasses(order);
    }

    private Set<Class<?>> getClasses(final List<ClassDepth<?>> orderValue) {
        final Set<Class<?>> newManagedClasses = new HashSet<>();
        for (ClassDepth<?> classDepth : orderValue) {
            newManagedClasses.add(classDepth.getType());
        }

        return newManagedClasses;
    }
}
