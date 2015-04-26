package com.kuro.random.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Created by Kumar Rohit on 4/22/15.
 */
public class JPAContext {

    private EntityManager entityManager;

    public static JPAContext newInstance(final EntityManager entityManager) {
        return new JPAContext(entityManager);
    }

    private JPAContext(final EntityManager entityManager) {
        this.entityManager = entityManager;


    }



}
