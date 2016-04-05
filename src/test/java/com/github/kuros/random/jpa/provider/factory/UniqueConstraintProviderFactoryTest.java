package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.mssql.MSSQLUniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.mysql.MySqlUniqueConstraintProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleUniqueConstraintProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UniqueConstraintProviderFactoryTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query query;
    @Mock
    private AttributeProvider attributeProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
    }

    @Test
    public void getUniqueCombinationAttributesForMsSqlServer() throws Exception {
        final UniqueConstraintProvider uniqueConstraintProvider = UniqueConstraintProviderFactory.getUniqueConstraintProvider(Database.MS_SQL_SERVER, entityManager, attributeProvider);
        assertTrue(uniqueConstraintProvider instanceof MSSQLUniqueConstraintProvider);
    }

    @Test
    public void getUniqueCombinationAttributesForMySql() throws Exception {
        final UniqueConstraintProvider uniqueConstraintProvider = UniqueConstraintProviderFactory.getUniqueConstraintProvider(Database.MY_SQL, entityManager, attributeProvider);
        assertTrue(uniqueConstraintProvider instanceof MySqlUniqueConstraintProvider);
    }

    @Test
    public void getUniqueCombinationAttributesForOracle() throws Exception {
        final UniqueConstraintProvider uniqueConstraintProvider = UniqueConstraintProviderFactory.getUniqueConstraintProvider(Database.ORACLE, entityManager, attributeProvider);
        assertTrue(uniqueConstraintProvider instanceof OracleUniqueConstraintProvider);
    }

    @Test
    public void getUniqueCombinationAttributesForNoDatabase() throws Exception {
        final UniqueConstraintProvider uniqueConstraintProvider = UniqueConstraintProviderFactory.getUniqueConstraintProvider(Database.NONE, entityManager, attributeProvider);
        assertTrue(uniqueConstraintProvider instanceof UniqueConstraintProviderFactory.DefaultUniqueConstraintProvider);

        assertEquals(0, uniqueConstraintProvider.getUniqueCombinationAttributes(Class.class).size());
    }
}
