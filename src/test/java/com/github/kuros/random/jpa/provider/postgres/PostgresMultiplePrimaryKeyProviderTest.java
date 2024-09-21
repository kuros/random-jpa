package com.github.kuros.random.jpa.provider.postgres;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostgresMultiplePrimaryKeyProviderTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private AttributeProvider attributeProvider;

    @Mock
    private Query query;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(new ArrayList());
    }


    @Test
    public void testMultiplePrimaryKeyQuery() {
        final String query = """
                SELECT t.table_name,
                  kcu.column_name
                FROM    INFORMATION_SCHEMA.TABLES t
                  LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
                    ON tc.table_catalog = t.table_catalog
                       AND tc.table_schema = t.table_schema
                       AND tc.table_name = t.table_name
                       AND tc.constraint_type = 'PRIMARY KEY'
                  LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
                    ON kcu.table_catalog = tc.table_catalog
                       AND kcu.table_schema = tc.table_schema
                       AND kcu.table_name = tc.table_name
                       AND kcu.constraint_name = tc.constraint_name
                WHERE   t.table_schema NOT IN ('pg_catalog', 'information_schema')
                ORDER BY t.table_catalog,
                  t.table_schema,
                  t.table_name,
                  kcu.constraint_name,
                  kcu.ordinal_position\
                """;

        final PostgresMultiplePrimaryKeyProvider primaryKeyProvider = (PostgresMultiplePrimaryKeyProvider) PostgresMultiplePrimaryKeyProvider.getInstance(entityManager, attributeProvider);

        assertEquals(query, primaryKeyProvider.getQuery());
    }
}