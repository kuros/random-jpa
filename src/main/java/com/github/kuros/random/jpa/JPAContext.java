package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Plan;

/**
 * Created by kumarro on 02/01/16.
 */
public interface JPAContext {
    CreationPlan create(Plan plan);

    ResultMap persist(CreationPlan creationPlan);

    ResultMap createAndPersist(Plan plan);

    <T, V> void remove(Class<T> type, V... ids);

    void remove(Class<?> type);

    void removeAll();
}
