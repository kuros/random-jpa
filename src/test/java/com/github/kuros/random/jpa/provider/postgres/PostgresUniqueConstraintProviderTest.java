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

public class PostgresUniqueConstraintProviderTest {

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
    public void testQueryForUniqueConstraint() {
        final String query = "SELECT table_name, column_name\n" +
                "FROM information_schema.constraint_column_usage WHERE constraint_schema not in ('pg_catalog', 'information_schema')";

        final PostgresUniqueConstraintProvider uniqueConstraintProvider = (PostgresUniqueConstraintProvider) PostgresUniqueConstraintProvider.getInstance(entityManager, attributeProvider);

        assertEquals(query, uniqueConstraintProvider.getQuery());

    }
}