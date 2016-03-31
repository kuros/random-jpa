package com.github.kuros.random.jpa.cache;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.MockedHierarchyGraph;
import com.github.kuros.random.jpa.types.Trigger;
import com.github.kuros.random.jpa.types.Version;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CacheTest {

    private TriggerCache triggerCache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        triggerCache = TriggerCache.getInstance(new ArrayList<Trigger<?>>());

    }

    @Test
    public void shouldInitializeCache() throws Exception {
        final EntityManager entityManager = EntityManagerProvider.getEntityManager();
        final Cache cache = Cache.create(Version.V2, Database.NONE, entityManager);

        cache.with(MockedHierarchyGraph.getHierarchyGraph());
        cache.with(triggerCache);
        cache.withSkipTruncations(new HashSet<Class<?>>());

        assertEquals(Version.V2, cache.getVersion());
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
