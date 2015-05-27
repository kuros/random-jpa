package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.testUtil.MockEntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.MockRelationshipProvider;
import com.github.kuros.random.jpa.testUtil.entity.Person;
import com.openpojo.random.RandomFactory;
import org.junit.Test;

import javax.persistence.EntityManager;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public class JPAContextTest {

    @Test
    public void should() {
        final MockEntityManagerProvider mockEntityManagerProvider = MockEntityManagerProvider.createMockEntityManager();
        final EntityManager entityManager = mockEntityManagerProvider.getEntityManager();
        MockRelationshipProvider.addMockRelationship(entityManager);

//        JPAContext jpaContext = JPAContext.create(entityManager);
//        jpaContext.create();

        final Person randomValue = RandomFactory.getRandomValue(Person.class);
        System.out.println(randomValue);
        System.out.println(randomValue.getFirstName());
        System.out.println(randomValue.getLastName());
    }
}
