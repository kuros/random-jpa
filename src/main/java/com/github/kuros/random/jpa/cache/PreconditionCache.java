package com.github.kuros.random.jpa.cache;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.link.Preconditions;
import com.github.kuros.random.jpa.types.Plan;

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
public final class PreconditionCache {

    private static PreconditionCache cache;
    private final Map<Class<?>, Plan> preConditionMap;

    private PreconditionCache(final Preconditions preconditions) {
        this.preConditionMap = preconditions.getPreConditionMap();
    }

    public static void init(final Preconditions preconditions) {
        if (cache == null) {
            cache = new PreconditionCache(preconditions);
        }
    }

    public static PreconditionCache getInstance() {
        if (cache == null) {
            throw new RandomJPAException("Cache should be initialized");
        }

        return cache;
    }

    public Plan getPlan(final Class<?> type) {
        final Plan plan = preConditionMap.get(type);
        return plan != null ? plan : Plan.create();
    }

    public Set<Class<?>> getIdentifiers() {
        return preConditionMap.keySet();
    }
}
