package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import org.junit.After;
import org.junit.Before;

import javax.persistence.EntityManager;

public class PersistedEntityResolverImplTest {

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        entityManager = EntityManagerProvider.getEntityManager();
    }



    @After
    public void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
