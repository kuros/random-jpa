package com.github.kuros.random.jpa.example.postgres;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.JPAContext;
import com.github.kuros.random.jpa.JPAContextFactory;
import com.github.kuros.random.jpa.example.postgres.entity.Sample;
import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.types.Entity;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

import static org.junit.Assert.*;

public class EntityManagerProviderTest {

    @Test
    public void createEntity() {
        final EntityManager entityManager = EntityManagerProvider.getEntityManager();

        final EntityTransaction transaction = entityManager.getTransaction();


        JPAContext jpaContext = JPAContextFactory.newInstance(Database.POSTGRES, entityManager)
                .generate();

        transaction.begin();
        final ResultMap persist = jpaContext.createAndPersist(Entity.of(Sample.class));
        transaction.commit();

        final Sample sample = persist.get(Sample.class);

        System.out.println(sample.getId());
        System.out.println(sample.getName());
    }
}