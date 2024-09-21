package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.h2.H2MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.mssql.MSSQLMultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.mysql.MySqlMultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleMultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.postgres.PostgresMultiplePrimaryKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiplePrimaryKeyProviderFactoryTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query query;
    @Mock
    private AttributeProvider attributeProvider;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
    }

    @Test
    public void getMultiplePrimaryKeyProviderForMsSQL() {
        final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider =
                MultiplePrimaryKeyProviderFactory.getMultiplePrimaryKeyProvider(Database.MS_SQL_SERVER, entityManager, attributeProvider);

        assertTrue(multiplePrimaryKeyProvider instanceof MSSQLMultiplePrimaryKeyProvider);
    }

    @Test
    public void getMultiplePrimaryKeyProviderForMySQL() {
        final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider =
                MultiplePrimaryKeyProviderFactory.getMultiplePrimaryKeyProvider(Database.MY_SQL, entityManager, attributeProvider);

        assertTrue(multiplePrimaryKeyProvider instanceof MySqlMultiplePrimaryKeyProvider);
    }

    @Test
    public void getMultiplePrimaryKeyProviderForOracle() {
        final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider =
                MultiplePrimaryKeyProviderFactory.getMultiplePrimaryKeyProvider(Database.ORACLE, entityManager, attributeProvider);

        assertTrue(multiplePrimaryKeyProvider instanceof OracleMultiplePrimaryKeyProvider);
    }

    @Test
    public void getMultiplePrimaryKeyProviderForPostgres() {
        final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider =
                MultiplePrimaryKeyProviderFactory.getMultiplePrimaryKeyProvider(Database.POSTGRES, entityManager, attributeProvider);

        assertTrue(multiplePrimaryKeyProvider instanceof PostgresMultiplePrimaryKeyProvider);
    }

    @Test
    public void getMultiplePrimaryKeyProviderForH2() {
        final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider =
                MultiplePrimaryKeyProviderFactory.getMultiplePrimaryKeyProvider(Database.H2, entityManager, attributeProvider);

        assertTrue(multiplePrimaryKeyProvider instanceof H2MultiplePrimaryKeyProvider);
    }

    @Test
    public void getMultiplePrimaryKeyProviderForNoDatabase() {
        final MultiplePrimaryKeyProvider multiplePrimaryKeyProvider =
                MultiplePrimaryKeyProviderFactory.getMultiplePrimaryKeyProvider(Database.NONE, entityManager, attributeProvider);

        assertTrue(multiplePrimaryKeyProvider instanceof MultiplePrimaryKeyProviderFactory.DefaultMultiplePrimaryKeyProvider);

        final List multiplePrimaryKeyAttributes = multiplePrimaryKeyProvider.getMultiplePrimaryKeyAttributes(Class.class);
        assertEquals(0, multiplePrimaryKeyAttributes.size());
    }
}
