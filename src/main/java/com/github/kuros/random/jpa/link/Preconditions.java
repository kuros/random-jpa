package com.github.kuros.random.jpa.link;

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

import com.github.kuros.random.jpa.types.Plan;

import java.util.HashMap;
import java.util.Map;

public class Preconditions {
    private final Map<Class<?>, Plan> preConditionMap;

    public Preconditions() {
        preConditionMap = new HashMap<Class<?>, Plan>();
    }

    public void add(final Class<?> type, final Plan plan) {
        preConditionMap.put(type, plan);
    }

    public Map<Class<?>, Plan> getPreConditionMap() {
        return preConditionMap;
    }
}
