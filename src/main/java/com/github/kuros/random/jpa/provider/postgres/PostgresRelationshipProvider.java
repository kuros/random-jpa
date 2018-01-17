package com.github.kuros.random.jpa.provider.postgres;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.provider.base.AbstractRelationshipProvider;

import javax.persistence.EntityManager;

public class PostgresRelationshipProvider extends AbstractRelationshipProvider {

    private static final String QUERY = "SELECT\n" +
            "  tc.table_name as p_table,\n" +
            "  kcu.column_name as p_column,\n" +
            "  ccu.table_name as r_table,\n" +
            "  ccu.column_name as r_column\n" +
            "FROM\n" +
            "  information_schema.table_constraints AS tc\n" +
            "  JOIN information_schema.key_column_usage AS kcu\n" +
            "    ON tc.constraint_name = kcu.constraint_name\n" +
            "  JOIN information_schema.constraint_column_usage AS ccu\n" +
            "    ON ccu.constraint_name = tc.constraint_name\n" +
            "WHERE tc.constraint_type = 'FOREIGN KEY'";

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
