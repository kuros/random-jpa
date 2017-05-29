package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.cleanup.Cleaner;
import com.github.kuros.random.jpa.cleanup.CleanerImpl;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.definition.MinimumHierarchyGenerator;
import com.github.kuros.random.jpa.persistor.EntityPersistorImpl;
import com.github.kuros.random.jpa.persistor.Persistor;
import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.persistor.model.ResultMapImpl;
import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.random.RandomizeImpl;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.resolver.CreationOrderResolver;
import com.github.kuros.random.jpa.resolver.CreationOrderResolverImpl;
import com.github.kuros.random.jpa.resolver.PersistedEntityResolver;
import com.github.kuros.random.jpa.types.AttributeValue;
import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.CreationPlanImpl;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.EntityHelper;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.util.MergeUtil;
import com.github.kuros.random.jpa.v1.resolver.CreationPlanResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
public final class JPAContextImpl implements JPAContext {

    private RandomGenerator generator;
    private Cache cache;

    private JPAContextImpl(final Cache cache, final Generator generator) {
        this.generator = RandomGenerator.newInstance(cache, generator);
        this.cache = cache;
    }

    public static JPAContext newInstance(final Cache cache,
                                         final Generator generator) {
        return new JPAContextImpl(cache, generator);
    }

    public CreationPlan create(final Entity... entities) {
        return create(Plan.of(entities));
    }

    public ResultMap persist(final CreationPlan creationPlan) {
        final CreationPlanImpl creationPlanImpl = (CreationPlanImpl) creationPlan;
        final Persistor persistor = EntityPersistorImpl.newInstance(cache, creationPlanImpl.getHierarchyGraph(), creationPlanImpl.getRandomize());
        return ResultMapImpl.newInstance(persistor.persist(creationPlan));
    }

    private Randomize getRandomizer() {
        return RandomizeImpl.newInstance(cache, generator);
    }

    public ResultMap createAndPersist(final Entity... entities) {
        return createAndPersist(Plan.of(entities));
    }

    public <T, V> void remove(final Class<T> type, final V... ids) {
        final Cleaner cleaner = CleanerImpl.newInstance(cache);
        cleaner.delete(type, ids);
    }

    public void remove(final Class<?> type) {
        final Cleaner cleaner = CleanerImpl.newInstance(cache);
        cleaner.truncate(type);
    }

    public void removeAll() {
        final Cleaner cleaner = CleanerImpl.newInstance(cache);
        cleaner.truncateAll();
    }

    @SuppressWarnings("unchecked")
    void addAttributeValues(final CreationPlan creationPlan, final List<Entity> entities) {
        for (Entity entity : entities) {
            final List<AttributeValue> attributeValues = EntityHelper.getAttributeValues(entity);
            for (AttributeValue attributeValue : attributeValues) {
                creationPlan.set(PersistedEntityResolver.DEFAULT_INDEX, attributeValue.getAttribute(), attributeValue.getValue());
            }
        }
    }

    private ResultMap createAndPersist(final Plan plan) {
        return persist(create(plan));
    }

    public RandomGenerator getGenerator() {
        return generator;
    }

    public Cache getCache() {
        return cache;
    }

    private CreationPlan create(final Plan plan) {

        final List<Entity> entities = plan.getEntities();
        final HierarchyGraph hierarchyGraph = MinimumHierarchyGenerator.generate(getCache().getHierarchyGraph(), entities);

        final CreationOrderResolver creationOrderResolver = CreationOrderResolverImpl.newInstance(hierarchyGraph);

        final List<CreationOrder> creationOrders = new ArrayList<CreationOrder>();
        for (Entity entity : entities) {
                final CreationOrder creationOrder = creationOrderResolver.getCreationOrder(entity);
                creationOrders.add(creationOrder);

        }

        final Collection<CreationOrder> values = MergeUtil.merge(creationOrders);
        sort(values);
        final CreationPlanResolver creationPlanResolver = CreationPlanResolver.newInstance(
                getRandomizer(), toArray(values));
        final CreationPlan creationPlan = creationPlanResolver.with(hierarchyGraph).create();
        addAttributeValues(creationPlan, entities);
        return creationPlan;
    }

    private void sort(final Collection<CreationOrder> values) {
        for (CreationOrder value : values) {
            Collections.sort(value.getOrder(), new Comparator<ClassDepth<?>>() {
                public int compare(final ClassDepth<?> o1, final ClassDepth<?> o2) {
                    return -1 * Integer.valueOf(o1.getDepth()).compareTo(o2.getDepth());
                }
            });
        }
    }

    private CreationOrder[] toArray(final Collection<CreationOrder> values) {
        final CreationOrder[] array = new CreationOrder[values.size()];
        return values.toArray(array);
    }
}
