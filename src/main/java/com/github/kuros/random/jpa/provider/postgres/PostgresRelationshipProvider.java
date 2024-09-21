package com.github.kuros.random.jpa.provider.postgres;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.provider.base.AbstractRelationshipProvider;

import jakarta.persistence.EntityManager;

public class PostgresRelationshipProvider extends AbstractRelationshipProvider {

    private static final String QUERY = """
            SELECT
              tc.table_name as p_table,
              kcu.column_name as p_column,
              ccu.table_name as r_table,
              ccu.column_name as r_column
            FROM
              information_schema.table_constraints AS tc
              JOIN information_schema.key_column_usage AS kcu
                ON tc.constraint_name = kcu.constraint_name
              JOIN information_schema.constraint_column_usage AS ccu
                ON ccu.constraint_name = tc.constraint_name
            WHERE tc.constraint_type = 'FOREIGN KEY'\
            """;

    @VisibleForTesting
    PostgresRelationshipProvider(EntityManager entityManager) {
        super(entityManager);
    }

    protected String getQuery() {
        return QUERY;
    }

    public static PostgresRelationshipProvider newInstance(final EntityManager entityManager) {
        return new PostgresRelationshipProvider(entityManager);
    }
}
