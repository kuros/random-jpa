package com.github.kuros.random.jpa.metamodel.providers.hibernate.v5;

import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.R;
import com.github.kuros.random.jpa.testUtil.entity.RelationEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class HibernateProviderV5Test {

    private EntityManager entityManager;

    private HibernateProviderV5 hibernateProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        entityManager = EntityManagerProvider.getEntityManager();
        hibernateProvider = new HibernateProviderV5(entityManager);
    }

    @Test
    public void shouldGenerateAttributeDetails() throws Exception {

        final EntityTableMapping entityTableMappingByClass = hibernateProvider.get(R.class);
        final List<EntityTableMapping> entityTableMappingByNames = hibernateProvider.get("R");

        assertEquals(1, entityTableMappingByNames.size());

        final EntityTableMapping entityTableMappingByName = entityTableMappingByNames.get(0);

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
    }

    @Test
    public void shouldGenerateAttributeDetailsForRelationalClasses() {

        final EntityTableMapping entityTableMapping = hibernateProvider.get(RelationEntity.class);
        assertNotNull(entityTableMapping);

        final Set<String> attributeNames = entityTableMapping.getAttributeNames();
        assertEquals(4, attributeNames.size());

        assertTrue(attributeNames.contains("id"));
        assertTrue(attributeNames.contains("relationOneToOne"));
        assertTrue(attributeNames.contains("relationManyToOne"));
        assertTrue(attributeNames.contains("relationOneToMany"));

        final Set<String> columnNames = entityTableMapping.getColumnNames();
        assertEquals(1, columnNames.size());

        assertTrue(columnNames.contains("id"));

    }

    @After
    public void tearDown() throws Exception {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

}