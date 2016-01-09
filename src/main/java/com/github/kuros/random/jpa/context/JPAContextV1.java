package com.github.kuros.random.jpa.context;

import com.github.kuros.random.jpa.JPAContext;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.resolver.CreationOrderResolver;
import com.github.kuros.random.jpa.resolver.CreationOrderResolverImpl;
import com.github.kuros.random.jpa.v1.resolver.CreationPlanResolver;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
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
public final class JPAContextV1 extends BaseContext {

    public static JPAContext newInstance(final Cache cache,
                                  final Generator generator) {
        return new JPAContextV1(cache, generator);
    }

    private JPAContextV1(final Cache cache, final Generator generator) {
        super(cache, generator);
    }

    public CreationPlan create(final Plan plan) {

        final CreationOrderResolver creationOrderResolver = CreationOrderResolverImpl.newInstance(getCache(), plan.getPreconditions());
        final CreationOrder creationOrder = creationOrderResolver.getCreationOrder(plan.getEntities());

        final CreationPlanResolver creationPlanResolver = CreationPlanResolver.newInstance(creationOrder, getRandomizer(plan));

        return creationPlanResolver.create();
    }

}
