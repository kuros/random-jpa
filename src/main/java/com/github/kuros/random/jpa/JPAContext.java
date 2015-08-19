package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.persistor.EntityPersistorImpl;
import com.github.kuros.random.jpa.persistor.Persistor;
import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.persistor.model.ResultMapImpl;
import com.github.kuros.random.jpa.random.RandomizeImpl;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.resolver.CreationOrderResolver;
import com.github.kuros.random.jpa.resolver.CreationOrderResolverImpl;
import com.github.kuros.random.jpa.resolver.CreationPlanResolver;
import com.github.kuros.random.jpa.resolver.EntityResolver;
import com.github.kuros.random.jpa.resolver.EntityResolverImpl;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.CreationPlanImpl;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.util.AttributeHelper;

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
public final class JPAContext {

    private HierarchyGraph hierarchyGraph;
    private RandomGenerator generator;
    private Cache cache;

    static JPAContext newInstance(final Cache cache,
                                  final Generator generator, final HierarchyGraph hierarchyGraph) {
        return new JPAContext( cache, generator, hierarchyGraph);
    }

    private JPAContext(final Cache cache, final Generator generator, final HierarchyGraph hierarchyGraph) {
        this.cache = cache;
        this.hierarchyGraph = hierarchyGraph;
        this.generator = RandomGenerator.newInstance(cache, generator);
    }

    public CreationPlan create(final Plan plan) {

        final RandomizeImpl randomize = getRandomizer(plan);
        final CreationOrderResolver creationOrderResolver = CreationOrderResolverImpl.newInstance(cache, hierarchyGraph, plan);
        final CreationOrder creationOrder = creationOrderResolver.getCreationOrder();

        final CreationPlanResolver creationPlanResolver = CreationPlanResolver.newInstance(creationOrder, randomize);

        return creationPlanResolver.create();
    }

    private RandomizeImpl getRandomizer(final Plan plan) {
        final RandomizeImpl randomize = RandomizeImpl.newInstance(cache, generator);
        final EntityResolver entityResolver = EntityResolverImpl.newInstance(cache, hierarchyGraph, plan);
        randomize.addFieldValue(entityResolver.getFieldValueMap());
        randomize.setNullValueFields(AttributeHelper.getFields(plan.getNullValueAttributes()));
        return randomize;
    }

    public ResultMap persist(final CreationPlan creationPlan) {
        final CreationPlanImpl creationPlanImpl = (CreationPlanImpl) creationPlan;
        final Persistor persistor = EntityPersistorImpl.newInstance(cache, creationPlanImpl.getRandomize());
        return ResultMapImpl.newInstance(persistor.persist(creationPlan));
    }

    public ResultMap createAndPersist(final Plan plan) {
        return persist(create(plan));
    }

}
