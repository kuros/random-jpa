package com.github.kuros.random.jpa.provider.mssql;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;
import com.github.kuros.random.jpa.testUtil.entity.Person;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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
public class MSSQLUniqueConstraintProviderTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private AttributeProvider attributeProvider;
    @Mock
    private Query query;

    private UniqueConstraintProvider uniqueConstraintProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockEntityManager();
        mockAttributeProvider();
        uniqueConstraintProvider = new MSSQLUniqueConstraintProvider(entityManager, attributeProvider);
    }

    @Test
    public void shouldMapAllAttributesToEntityName() throws Exception {
        final List<String> uniqueCombinationAttributes = uniqueConstraintProvider.getUniqueCombinationAttributes(Person.class);
        assertEquals(2, uniqueCombinationAttributes.size());
        assertTrue(uniqueCombinationAttributes.contains("firstName"));
        assertTrue(uniqueCombinationAttributes.contains("lastName"));
    }

    private void mockAttributeProvider() {
        final EntityTableMapping personTableMapping = new EntityTableMapping(Person.class);
        personTableMapping.addAttributeColumnMapping("firstName", "first_name");
        personTableMapping.addAttributeColumnMapping("lastName", "last_name");
        final List<EntityTableMapping> entityTableMappings = new ArrayList<EntityTableMapping>();
        entityTableMappings.add(personTableMapping);
        when(attributeProvider.get(eq("person"))).thenReturn(entityTableMappings);
    }

    private void mockEntityManager() {
        final Object[] row1 = {"person", "first_name"};
        final Object[] row2 = {"person", "last_name"};

        final List<Object[]> resultList = new ArrayList<Object[]>();
        resultList.add(row1);
        resultList.add(row2);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(resultList);
    }
}
