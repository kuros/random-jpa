package com.kuro.random.jpa.persistor;

import com.kuro.random.jpa.persistor.model.ResultMap;
import com.kuro.random.jpa.types.CreationPlan;

import java.util.Map;

/**
 * Created by Kumar Rohit on 5/13/15.
 */
public interface Persistor {

    ResultMap persist(CreationPlan creationPlan);
}
