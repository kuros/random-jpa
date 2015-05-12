package com.kuro.random.jpa;

import com.kuro.random.jpa.testUtil.MockEntityManagerProvider;
import com.kuro.random.jpa.testUtil.MockRelationshipProvider;
import org.junit.Test;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public class JPAContextTest {

    @Test
    public void should() {
        final MockEntityManagerProvider mockEntityManagerProvider = MockEntityManagerProvider.createMockEntityManager();
        final EntityManager entityManager = mockEntityManagerProvider.getEntityManager();
        MockRelationshipProvider.addMockRelationship(entityManager);

        JPAContext jpaContext = JPAContext.newInstance(entityManager);
        jpaContext.create();
    }
}