package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import jakarta.persistence.EntityManager;

public class PersistedEntityResolverImplTest {

    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws Exception {
        entityManager = EntityManagerProvider.getEntityManager();
    }



    @AfterEach
    public void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
