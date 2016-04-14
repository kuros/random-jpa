package com.github.kuros.random.jpa.provider.mssql;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.testUtil.entity.Department;
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
public class MSSQLCharacterLengthProviderTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private AttributeProvider attributeProvider;
    @Mock
    private Query query;
    private SQLCharacterLengthProvider sqlCharacterLengthProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockEntityManager();
        mockAttributeProvider();
        sqlCharacterLengthProvider = new MSSQLCharacterLengthProvider(entityManager, attributeProvider);
    }

    @Test
    public void testApplyLengthConstraint() throws Exception {
        assertEquals("abcde", sqlCharacterLengthProvider.applyLengthConstraint(Department.class.getName(), "departmentName", "abcdefghijkl"));
        assertEquals("feature yet not implemented for mssql", 789, sqlCharacterLengthProvider.applyLengthConstraint(Department.class.getName(), "departmentId", 123456789));
    }

    private void mockAttributeProvider() {
        final EntityTableMapping personTableMapping = new EntityTableMapping(Person.class);

        personTableMapping.addAttributeColumnMapping("firstName", "first_name");
        personTableMapping.addAttributeColumnMapping("lastName", "last_name");
        final List<EntityTableMapping> entityTableMappings = new ArrayList<EntityTableMapping>();
        entityTableMappings.add(personTableMapping);
        when(attributeProvider.get(eq("person"))).thenReturn(entityTableMappings);

        final EntityTableMapping departmentTableMapping = new EntityTableMapping(Department.class);
        departmentTableMapping.addAttributeColumnMapping("departmentId", "department_id");
        departmentTableMapping.addAttributeColumnMapping("departmentName", "department_name");

        final List<EntityTableMapping> departmentEntityTableMappings = new ArrayList<EntityTableMapping>();
        departmentEntityTableMappings.add(departmentTableMapping);
        when(attributeProvider.get(eq("department"))).thenReturn(departmentEntityTableMappings);
    }

    private void mockEntityManager() {
        final Object[] row1 = {"person", "first_name", 10, null, null, "varchar"};
        final Object[] row2 = {"person", "last_name", 20, null, null, "varchar"};
        final Object[] row3 = {"department", "department_name", 5, null, null, "varchar"};
        final Object[] row4 = {"employee", "employee_name", 10, null, null, "varchar"};
        final Object[] row5 = {"department", "department_id", null, (byte)3, 2, "decimal"};

        final List<Object[]> resultList = new ArrayList<Object[]>();
        resultList.add(row1);
        resultList.add(row2);
        resultList.add(row3);
        resultList.add(row4);
        resultList.add(row5);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(resultList);
    }
}
