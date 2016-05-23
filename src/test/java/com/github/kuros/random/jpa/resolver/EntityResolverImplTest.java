package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.MockedHierarchyGraph;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.util.Util;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class EntityResolverImplTest {

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        entityManager = EntityManagerProvider.getEntityManager();
    }

    @Test
    public void shouldPopulateFieldValueForExistingHierarchy() throws Exception {

        final Cache cache = Cache.create(Database.NONE, entityManager);
        cache.with(MockedHierarchyGraph.getHierarchyGraph());

        final X x = new X();
        EntityManagerProvider.persist(x);

        final Y y = new Y();
        EntityManagerProvider.persist(y);

        final Z z = new Z();
        z.setxId(x.getId());
        z.setyId(y.getId());
        EntityManagerProvider.persist(z);

        final Plan plan = Plan.of(Entity.of(Z.class).with(Z_.id, z.getId()));
        final EntityResolver entityResolver = EntityResolverImpl.newInstance(cache, cache.getHierarchyGraph());
        final Map<Field, Object> fieldValueMap = entityResolver.getFieldValues(plan);

        assertEquals(z.getId(), fieldValueMap.get(Util.getField(Z.class, "id")));
        assertEquals(x.getId(), fieldValueMap.get(Util.getField(X.class, "id")));
        assertEquals(y.getId(), fieldValueMap.get(Util.getField(Y.class, "id")));

    }

    @Test
    public void shouldNotPopulateFieldValueForExistingHierarchyWhenNoIdIsProvided() throws Exception {

        final Cache cache = Cache.create(Database.NONE, entityManager);
        cache.with(MockedHierarchyGraph.getHierarchyGraph());

        final X x = new X();
        EntityManagerProvider.persist(x);

        final Y y = new Y();
        EntityManagerProvider.persist(y);

        final Z z = new Z();
        z.setxId(x.getId());
        z.setyId(y.getId());
        EntityManagerProvider.persist(z);

        final Plan plan = Plan.of(Entity.of(Z.class).with(Z_.id, null));
        final EntityResolver entityResolver = EntityResolverImpl.newInstance(cache, cache.getHierarchyGraph());
        final Map<Field, Object> fieldValueMap = entityResolver.getFieldValues(plan);

        assertNull(fieldValueMap.get(Util.getField(Z.class, "id")));
        assertFalse(fieldValueMap.keySet().contains((Util.getField(X.class, "id"))));
        assertFalse(fieldValueMap.keySet().contains((Util.getField(Y.class, "id"))));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfWrongIdProvided() throws Exception {

        final Cache cache = Cache.create(Database.NONE, entityManager);
        cache.with(MockedHierarchyGraph.getHierarchyGraph());

        final Plan plan = Plan.of(Entity.of(Z.class).with(Z_.id, RandomFixture.create(Long.class)));
        final EntityResolver entityResolver = EntityResolverImpl.newInstance(cache, cache.getHierarchyGraph());
        entityResolver.getFieldValues(plan);

    }

    @After
    public void tearDown() throws Exception {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
