package com.github.kuros.random.jpa.persistor;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.cache.TriggerCache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.persistor.model.ResultNodeTree;
import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.random.RandomizeImpl;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.RelationEntity;
import com.github.kuros.random.jpa.testUtil.entity.RelationManyToOne;
import com.github.kuros.random.jpa.testUtil.entity.RelationOneToOne;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.MockedHierarchyGraph;
import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Trigger;
import com.github.kuros.random.jpa.types.Version;
import com.github.kuros.random.jpa.v1.resolver.CreationPlanResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
public class EntityPersistorImplTest {

    private Cache cache;
    private HierarchyGraph hierarchyGraph;
    private EntityManager entityManager;
    private Randomize randomize;
    private Persistor unit;

    @Before
    public void setUp() throws Exception {
        hierarchyGraph = MockedHierarchyGraph.getHierarchyGraph();
        entityManager = EntityManagerProvider.getEntityManager();
        cache = Cache.create(Version.V2, Database.NONE, entityManager);

        final TriggerCache triggerCache = TriggerCache.getInstance(new ArrayList<Trigger<?>>());
        cache.with(triggerCache);

        cache.with(hierarchyGraph);

        randomize = RandomizeImpl.newInstance(cache, RandomGenerator.newInstance(cache));

        unit = EntityPersistorImpl.newInstance(cache, hierarchyGraph, randomize);
    }


    @Test
    public void  shouldPersistCreationPlan() {
        final CreationPlan creationPlan = getCreationPlan();

        entityManager.getTransaction().begin();
        final ResultNodeTree result = unit.persist(creationPlan);
        entityManager.getTransaction().commit();

        assertNotNull(result);
        final X x = result.get(X.class);
        assertNotNull(x);

        final Y y = result.get(Y.class);
        assertNotNull(y);

        final Z z = result.get(Z.class);
        assertNotNull(z);

        assertEquals(x.getId(), z.getxId());
        assertEquals(y.getId(), z.getyId());
    }

    @Test
    public void shouldPersistCreationPlanWithCount() {
        final CreationPlan creationPlan = getCreationPlanWithCounts(1, 2, 2);

        entityManager.getTransaction().begin();
        final ResultNodeTree result = unit.persist(creationPlan);
        entityManager.getTransaction().commit();

        assertNotNull(result);
        final List<X> xValues = result.getAll(X.class);
        assertEquals(1, xValues.size());

        final List<Y> yValues = result.getAll(Y.class);
        assertEquals(2, yValues.size());

        final List<Z> zValues = result.getAll(Z.class);
        assertEquals(4, zValues.size());

        for (Z zValue : zValues) {
            assertEquals(xValues.get(0).getId(), zValue.getxId());
        }

        assertEquals(yValues.get(0).getId(), zValues.get(0).getyId());
        assertEquals(yValues.get(0).getId(), zValues.get(1).getyId());
        assertEquals(yValues.get(1).getId(), zValues.get(2).getyId());
        assertEquals(yValues.get(1).getId(), zValues.get(3).getyId());
    }

    @Test
    public void shouldObjectValueForMappedRelations() throws Exception {
        final CreationPlan creationPlan = CreationPlanResolver
                .newInstance(randomize, getRelationEntityCreationOrder())
                .create();

        entityManager.getTransaction().begin();
        final ResultNodeTree result = unit.persist(creationPlan);
        entityManager.getTransaction().commit();

        final RelationOneToOne relationOneToOne = result.get(RelationOneToOne.class);
        assertNotNull(relationOneToOne);
        final RelationManyToOne relationManyToOne  = result.get(RelationManyToOne.class);
        assertNotNull(relationManyToOne);

        final RelationEntity relationEntity = result.get(RelationEntity.class);
        assertNotNull(relationEntity);

        assertEquals(relationEntity.getRelationManyToOne().getId(), relationManyToOne.getId());
        assertEquals(relationEntity.getRelationOneToOne().getId(), relationOneToOne.getId());
    }

    private CreationPlan getCreationPlanWithCounts(final int xCount, final int yCount, final int zCount) {
        final CreationOrder creationOrder = getCreationOrder();
        creationOrder.addCreationCount(X.class, xCount);
        creationOrder.addCreationCount(Y.class, yCount);
        creationOrder.addCreationCount(Z.class, zCount);
        return CreationPlanResolver.newInstance(randomize, creationOrder).create();
    }

    private CreationPlan getCreationPlan() {
        final CreationOrder creationOrder = getCreationOrder();

        return CreationPlanResolver.newInstance(randomize, creationOrder).create();
    }

    private CreationOrder getCreationOrder() {
        final CreationOrder creationOrder = CreationOrder.newInstance();
        creationOrder.add(ClassDepth.newInstance(X.class, 0));
        creationOrder.add(ClassDepth.newInstance(Y.class, 0));
        creationOrder.add(ClassDepth.newInstance(Z.class, 1));
        return creationOrder;
    }

    private CreationOrder getRelationEntityCreationOrder() {
        final CreationOrder creationOrder = CreationOrder.newInstance();
        creationOrder.add(ClassDepth.newInstance(RelationOneToOne.class, 0));
        creationOrder.add(ClassDepth.newInstance(RelationManyToOne.class, 0));
        creationOrder.add(ClassDepth.newInstance(RelationEntity.class, 0));

        return creationOrder;
    }

    @After
    public void tearDown() throws Exception {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
