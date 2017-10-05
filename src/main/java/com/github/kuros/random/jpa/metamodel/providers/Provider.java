package com.github.kuros.random.jpa.metamodel.providers;

import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;

import java.util.List;

public interface Provider {

    EntityTableMapping get(final Class<?> type);

    List<EntityTableMapping> get(final String tableName);
}
