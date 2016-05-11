package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.types.ClassIndex;
import com.github.kuros.random.jpa.types.CreationPlan;

import java.util.Map;

public interface PersistedEntityResolver {

    int DEFAULT_INDEX = -1;
    Map<ClassIndex, Object> loadPersistedObjectByIds(CreationPlan creationPlan);

}
