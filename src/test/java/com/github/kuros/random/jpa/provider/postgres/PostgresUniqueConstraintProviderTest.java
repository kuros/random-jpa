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

public class PostgresUniqueConstraintProviderTest {

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
    public void testQueryForUniqueConstraint() {
        final String query = """
                SELECT table_name, column_name
                FROM information_schema.constraint_column_usage WHERE constraint_schema not in ('pg_catalog', 'information_schema')\
                """;

        final PostgresUniqueConstraintProvider uniqueConstraintProvider = (PostgresUniqueConstraintProvider) PostgresUniqueConstraintProvider.getInstance(entityManager, attributeProvider);

        assertEquals(query, uniqueConstraintProvider.getQuery());

    }
}