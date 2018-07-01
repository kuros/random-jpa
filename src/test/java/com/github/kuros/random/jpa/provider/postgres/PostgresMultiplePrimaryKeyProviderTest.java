package com.github.kuros.random.jpa.provider.postgres;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class PostgresMultiplePrimaryKeyProviderTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private AttributeProvider attributeProvider;

    @Mock
    private Query query;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(new ArrayList());
    }


    @Test
    public void testMultiplePrimaryKeyQuery() {
        final String query = "SELECT t.table_name,\n" +
                "  kcu.column_name\n" +
                "FROM    INFORMATION_SCHEMA.TABLES t\n" +
                "  LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc\n" +
                "    ON tc.table_catalog = t.table_catalog\n" +
                "       AND tc.table_schema = t.table_schema\n" +
                "       AND tc.table_name = t.table_name\n" +
                "       AND tc.constraint_type = 'PRIMARY KEY'\n" +
                "  LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu\n" +
                "    ON kcu.table_catalog = tc.table_catalog\n" +
                "       AND kcu.table_schema = tc.table_schema\n" +
                "       AND kcu.table_name = tc.table_name\n" +
                "       AND kcu.constraint_name = tc.constraint_name\n" +
                "WHERE   t.table_schema NOT IN ('pg_catalog', 'information_schema')\n" +
                "ORDER BY t.table_catalog,\n" +
                "  t.table_schema,\n" +
                "  t.table_name,\n" +
                "  kcu.constraint_name,\n" +
                "  kcu.ordinal_position";

        final PostgresMultiplePrimaryKeyProvider primaryKeyProvider = (PostgresMultiplePrimaryKeyProvider) PostgresMultiplePrimaryKeyProvider.getInstance(entityManager, attributeProvider);

        assertEquals(query, primaryKeyProvider.getQuery());
    }
}