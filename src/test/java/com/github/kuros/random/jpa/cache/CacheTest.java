package com.github.kuros.random.jpa.cache;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.MockedHierarchyGraph;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;

public class CacheTest {

    private TriggerCache triggerCache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        triggerCache = TriggerCache.getInstance(new ArrayList<>());

    }

    @Test
    public void shouldInitializeCache() {
        final EntityManager entityManager = EntityManagerProvider.getEntityManager();
        final Cache cache = Cache.create(Database.NONE, entityManager);

        cache.with(MockedHierarchyGraph.getHierarchyGraph());
        cache.with(triggerCache);
        cache.withSkipTruncations(new HashSet<>());

        assertNotNull(cache.getAttributeProvider());
        assertNotNull(cache.getEntityManager());
        assertNotNull(cache.getHierarchyGraph());
        assertNotNull(cache.getMultiplePrimaryKeyProvider());
        assertNotNull(cache.getRelationshipProvider());
        assertNotNull(cache.getSkipTruncation());
        assertNotNull(cache.getSqlCharacterLengthProvider());
        assertNotNull(cache.getTriggerCache());
        assertNotNull(cache.getUniqueConstraintProvider());
    }
}
