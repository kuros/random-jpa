package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.persistor.EntityPersistorImpl;
import com.github.kuros.random.jpa.persistor.Persistor;
import com.github.kuros.random.jpa.persistor.model.ResultMap;
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

import javax.persistence.EntityManager;

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

    private final EntityManager entityManager;
    private HierarchyGraph hierarchyGraph;
    private RandomGenerator generator;

    static JPAContext newInstance(final EntityManager entityManager,
                                  final Generator generator, final HierarchyGraph hierarchyGraph) {
        return new JPAContext( entityManager, generator, hierarchyGraph);
    }

    private JPAContext(final EntityManager entityManager, final Generator generator, final HierarchyGraph hierarchyGraph) {
        this.entityManager = entityManager;
        this.hierarchyGraph = hierarchyGraph;
        this.generator = RandomGenerator.newInstance(generator);
    }

    public CreationPlan create(final Plan plan) {

        final RandomizeImpl randomize = getRandomizer(plan);
        final CreationOrderResolver creationOrderResolver = CreationOrderResolverImpl.newInstance(hierarchyGraph, plan);
        final CreationOrder creationOrder = creationOrderResolver.getCreationOrder();

        final CreationPlanResolver creationPlanResolver = CreationPlanResolver.newInstance(creationOrder, randomize);

        return creationPlanResolver.create();
    }

    private RandomizeImpl getRandomizer(final Plan plan) {
        final RandomizeImpl randomize = RandomizeImpl.newInstance(generator);
        final EntityResolver entityResolver = EntityResolverImpl.newInstance(hierarchyGraph, plan);
        randomize.addFieldValue(entityResolver.getFieldValueMap());
        randomize.setNullValueFields(AttributeHelper.getFields(plan.getNullValueAttributes()));
        return randomize;
    }

    public ResultMap persist(final CreationPlan creationPlan) {
        final CreationPlanImpl creationPlanImpl = (CreationPlanImpl) creationPlan;
        final Persistor persistor = EntityPersistorImpl.newInstance(creationPlanImpl.getRandomize());
        return persistor.persist(creationPlan);
    }

    public ResultMap createAndPersist(final Plan plan) {
        return persist(create(plan));
    }

}
