package com.github.kuros.random.jpa.persistor.hepler;

import javax.persistence.EntityManager;

public interface EntityManagerWithNoLock {

    EntityManager getEntityManager();

}
