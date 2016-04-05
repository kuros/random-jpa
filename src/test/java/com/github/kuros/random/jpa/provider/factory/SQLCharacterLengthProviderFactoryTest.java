package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.mssql.MSSQLCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.mysql.MySqlCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleCharacterLengthProvider;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQLCharacterLengthProviderFactoryTest {

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
    public void getSqlCharacterLengthProviderForMsSqlServer() throws Exception {
        final SQLCharacterLengthProvider sqlCharacterLengthProvider = SQLCharacterLengthProviderFactory.getSqlCharacterLengthProvider(Database.MS_SQL_SERVER, entityManager, attributeProvider);
        assertTrue(sqlCharacterLengthProvider instanceof MSSQLCharacterLengthProvider);
    }

    @Test
    public void getSqlCharacterLengthProviderForMySql() throws Exception {
        final SQLCharacterLengthProvider sqlCharacterLengthProvider = SQLCharacterLengthProviderFactory.getSqlCharacterLengthProvider(Database.MY_SQL, entityManager, attributeProvider);
        assertTrue(sqlCharacterLengthProvider instanceof MySqlCharacterLengthProvider);
    }

    @Test
    public void getSqlCharacterLengthProviderForOracle() throws Exception {
        final SQLCharacterLengthProvider sqlCharacterLengthProvider = SQLCharacterLengthProviderFactory.getSqlCharacterLengthProvider(Database.ORACLE, entityManager, attributeProvider);
        assertTrue(sqlCharacterLengthProvider instanceof OracleCharacterLengthProvider);
    }

    @Test
    public void getSqlCharacterLengthProviderForNoDatabase() throws Exception {
        final SQLCharacterLengthProvider sqlCharacterLengthProvider = SQLCharacterLengthProviderFactory.getSqlCharacterLengthProvider(Database.NONE, entityManager, attributeProvider);
        assertTrue(sqlCharacterLengthProvider instanceof SQLCharacterLengthProviderFactory.DefaultSQLCharacterLengthProvider);

        final SQLCharacterLengthProviderFactory.DefaultSQLCharacterLengthProvider provider = (SQLCharacterLengthProviderFactory.DefaultSQLCharacterLengthProvider) sqlCharacterLengthProvider;
        assertEquals(10, provider.getMaxLength(null, null).intValue());
        final String randomString = RandomFixture.create(String.class);
        assertEquals(randomString, provider.applyLengthConstraint(null, null, randomString));
    }
}
