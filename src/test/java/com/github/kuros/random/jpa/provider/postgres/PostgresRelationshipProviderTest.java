package com.github.kuros.random.jpa.provider.postgres;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;

public class PostgresRelationshipProviderTest {

    @Mock
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueryForPostgress() {
        final String expectedQuery = "SELECT\n" +
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

        PostgresRelationshipProvider postgresRelationshipProvider = PostgresRelationshipProvider.newInstance(entityManager);

        assertEquals(expectedQuery, postgresRelationshipProvider.getQuery());
    }
}