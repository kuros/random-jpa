package com.kuro.random.jpa;

import com.kuro.random.jpa.testUtil.MockEntityManagerProvider;
import com.kuro.random.jpa.testUtil.MockRelationshipProvider;
import com.kuro.random.jpa.testUtil.entity.Person;
import com.openpojo.random.RandomFactory;
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

//        JPAContext jpaContext = JPAContext.newInstance(entityManager);
//        jpaContext.create();

        final Person randomValue = RandomFactory.getRandomValue(Person.class);
        System.out.println(randomValue);
        System.out.println(randomValue.getFirstName());
        System.out.println(randomValue.getLastName());
    }
}