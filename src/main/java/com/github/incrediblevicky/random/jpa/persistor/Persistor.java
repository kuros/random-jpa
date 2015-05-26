package com.github.incrediblevicky.random.jpa.persistor;

import com.github.incrediblevicky.random.jpa.persistor.model.ResultMap;
import com.github.incrediblevicky.random.jpa.types.CreationPlan;

/**
 * Created by Kumar Rohit on 5/13/15.
 */
public interface Persistor {

    ResultMap persist(CreationPlan creationPlan);
}
