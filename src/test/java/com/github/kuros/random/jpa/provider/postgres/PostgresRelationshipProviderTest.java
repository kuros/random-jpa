package com.github.kuros.random.jpa.provider.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostgresRelationshipProviderTest {

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueryForPostgress() {
        final String expectedQuery = """
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

        PostgresRelationshipProvider postgresRelationshipProvider = PostgresRelationshipProvider.newInstance(entityManager);

        assertEquals(expectedQuery, postgresRelationshipProvider.getQuery());
    }
}