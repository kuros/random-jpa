package com.github.kuros.random.jpa.link;

import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;

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
public final class Before {

    private Class<?> type;
    private Plan plan;

    private Before(final Class<?> type) {
        this.type = type;
        this.plan = Plan.create();
    }

    public static Before of(final Class<?> type) {
        return new Before(type);
    }

    public Before create(final Class<?>... types) {
        for (Class<?> type : types) {
            plan.add(Entity.of(type));
        }

        return this;
    }

    public Class<?> getType() {
        return type;
    }

    public Plan getPlan() {
        return plan;
    }
}
