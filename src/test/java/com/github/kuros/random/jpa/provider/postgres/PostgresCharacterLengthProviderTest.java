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

public class PostgresCharacterLengthProviderTest {

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
    public void testQueryToFetchCharacterLengths() {
        final String expectedQuery = """
                SELECT table_name, column_name, character_maximum_length, numeric_precision, numeric_scale, data_type
                FROM information_schema.columns WHERE table_schema not in ('pg_catalog', 'information_schema')\
                """;

        PostgresCharacterLengthProvider characterLengthProvider = PostgresCharacterLengthProvider.getInstance(entityManager, attributeProvider);
        assertEquals(expectedQuery, characterLengthProvider.getQuery());
    }
}