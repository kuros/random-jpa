package com.github.kuros.random.jpa.persistor;

import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.types.CreationPlan;

/**
 * Created by Kumar Rohit on 5/13/15.
 */
public interface Persistor {

    ResultMap persist(CreationPlan creationPlan);
}
