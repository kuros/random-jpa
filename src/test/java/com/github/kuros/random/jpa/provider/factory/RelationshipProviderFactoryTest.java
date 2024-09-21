package com.github.kuros.random.jpa.provider.factory;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import com.github.kuros.random.jpa.provider.h2.H2RelationshipProvider;
import com.github.kuros.random.jpa.provider.model.ForeignKeyRelation;
import com.github.kuros.random.jpa.provider.mssql.MSSQLRelationshipProvider;
import com.github.kuros.random.jpa.provider.mysql.MySqlRelationshipProvider;
import com.github.kuros.random.jpa.provider.oracle.OracleRelationshipProvider;
import com.github.kuros.random.jpa.provider.postgres.PostgresRelationshipProvider;
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

public class RelationshipProviderFactoryTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query query;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);

    }

    @Test
    public void getRelationshipProviderForMsSQL() {
        final RelationshipProvider relationshipProvider = RelationshipProviderFactory.getRelationshipProvider(Database.MS_SQL_SERVER, entityManager);
        assertTrue(relationshipProvider instanceof MSSQLRelationshipProvider);
    }

    @Test
    public void getRelationshipProviderForMySQL() {
        final RelationshipProvider relationshipProvider = RelationshipProviderFactory.getRelationshipProvider(Database.MY_SQL, entityManager);
        assertTrue(relationshipProvider instanceof MySqlRelationshipProvider);
    }

    @Test
    public void getRelationshipProviderForOracle() {
        final RelationshipProvider relationshipProvider = RelationshipProviderFactory.getRelationshipProvider(Database.ORACLE, entityManager);
        assertTrue(relationshipProvider instanceof OracleRelationshipProvider);
    }

    @Test
    public void getRelationshipProviderForPostgres() {
        final RelationshipProvider relationshipProvider = RelationshipProviderFactory.getRelationshipProvider(Database.POSTGRES, entityManager);
        assertTrue(relationshipProvider instanceof PostgresRelationshipProvider);
    }

    @Test
    public void getRelationshipProviderForH2() {
        final RelationshipProvider relationshipProvider = RelationshipProviderFactory.getRelationshipProvider(Database.H2, entityManager);
        assertTrue(relationshipProvider instanceof H2RelationshipProvider);
    }

    @Test
    public void getRelationshipProviderForNoDatabase() {
        final RelationshipProvider relationshipProvider = RelationshipProviderFactory.getRelationshipProvider(Database.NONE, entityManager);
        assertTrue(relationshipProvider instanceof RelationshipProviderFactory.EmptyRelationshipProvider);

        final List<ForeignKeyRelation> foreignKeyRelations = relationshipProvider.getForeignKeyRelations();
        assertEquals(0, foreignKeyRelations.size());
    }
}
