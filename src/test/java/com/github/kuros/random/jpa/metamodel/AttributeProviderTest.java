package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.AssingedId;
import com.github.kuros.random.jpa.testUtil.entity.R;
import org.hibernate.id.Assigned;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AttributeProviderTest {

    private EntityManager entityManager;
    @Mock private SingleTableEntityPersister singleTableEntityPersister;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        entityManager = EntityManagerProvider.getEntityManager();
    }

    @Test
    public void shouldGenerateAttributeDetails() throws Exception {
        final AttributeProvider attributeProvider = AttributeProvider.getInstance(entityManager);

        final EntityTableMapping entityTableMappingByClass = attributeProvider.get(R.class);
        final EntityTableMapping entityTableMappingByName = attributeProvider.get("R");

        assertEquals(entityTableMappingByClass, entityTableMappingByName);

        final List<String> attributeIds = entityTableMappingByClass.getAttributeIds();
        assertEquals(1, attributeIds.size());
        assertTrue(attributeIds.contains("id"));

        final List<String> columnIds = entityTableMappingByClass.getColumnIds();
        assertEquals(1, columnIds.size());
        assertTrue(columnIds.contains("id"));

        assertEquals("pId", entityTableMappingByClass.getAttributeName("p_id"));
        assertEquals("p_id", entityTableMappingByClass.getColumnName("pId"));

        final Set<String> attributeNames = entityTableMappingByClass.getAttributeNames();
        assertEquals(2, attributeNames.size());
        assertTrue(attributeNames.contains("id"));
        assertTrue(attributeNames.contains("pId"));

        final Set<String> columnNames = entityTableMappingByClass.getColumnNames();
        assertEquals(2, columnNames.size());
        assertTrue(columnNames.contains("id"));
        assertTrue(columnNames.contains("p_id"));

        assertEquals(R.class, entityTableMappingByClass.getEntityClass());
        assertEquals("com.github.kuros.random.jpa.testUtil.entity.R", entityTableMappingByClass.getEntityName());
        assertEquals("r", entityTableMappingByClass.getTableName());

        assertEquals(IdentityGenerator.class, entityTableMappingByClass.getIdentifierGenerator());
    }

    @Test
    public void shouldSetIdentityGeneratorAssignedIfNoGeneratorTypeIsMentionedInEntity() throws Exception {
        final AttributeProvider attributeProvider = AttributeProvider.getInstance(entityManager);
        final EntityTableMapping entityTableMapping = attributeProvider.get(AssingedId.class);
        assertEquals(Assigned.class, entityTableMapping.getIdentifierGenerator());
    }

    @Test
    public void validateListOfUnsupportedIdentifierGenerator() throws Exception {
        final AttributeProvider attributeProvider = AttributeProvider.getInstance(entityManager);
        final Set<Class<?>> unSupportedGeneratorType = attributeProvider.getUnSupportedGeneratorType();

        assertEquals(1, unSupportedGeneratorType.size());
        assertTrue(unSupportedGeneratorType.contains(Assigned.class));

    }

    @Test
    public void shouldParseTableIfSchemaIsProvided() throws Exception {
        Mockito.when(singleTableEntityPersister.getTableName()).thenReturn("someSchema.someTableName");
        final AttributeProvider attributeProvider = AttributeProvider.getInstance(entityManager);

        final String tableName = attributeProvider.getTableName(singleTableEntityPersister);
        assertEquals("sometablename", tableName);

    }

    @After
    public void tearDown() throws Exception {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
