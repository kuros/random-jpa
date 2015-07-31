package com.github.kuros.random.jpa.cache;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.types.Trigger;

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
public final class TriggerCache {
    private static TriggerCache cache;
    private Map<Class<?>, Trigger<?>> triggerMap;

    private TriggerCache(final List<Trigger<?>> triggers) {
        this.triggerMap = new HashMap<Class<?>, Trigger<?>>();
        for (Trigger<?> trigger : triggers) {
            triggerMap.put(trigger.getTriggerClass(), trigger);
        }
    }

    public static void init(final List<Trigger<?>> triggers) {
        cache = new TriggerCache(triggers);
    }

    public static TriggerCache getInstance() {
        if (cache == null) {
            throw new RandomJPAException("Cache should be initialized");
        }

        return cache;
    }

    public boolean contains(final Class<?> type) {
        return triggerMap.containsKey(type);
    }

    public Trigger<?> getTrigger(final Class<?> type) {
        return triggerMap.get(type);
    }
}
