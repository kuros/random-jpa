package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.testUtil.entity.Employee;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
public class EntityTypeHelperTest {

    @Mock
    private EntityType entityType;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(entityType.getJavaType()).thenReturn(Employee.class);
    }

    @Test
    public void shouldFindFieldIfNameIsProvidedInColumn() {
        final Field employeeId = EntityTypeHelper.getField(entityType, "employee_id");
        assertEquals("employeeId", employeeId.getName());
    }

    @Test
    public void shouldFindFieldIfNameIsNotProvidedInColumn() {
        final Field salary = EntityTypeHelper.getField(entityType, "salary");
        assertEquals("salary", salary.getName());
    }

    @Test
    public void shouldReturnNullIfFieldNotFound() {
        final Field salary = EntityTypeHelper.getField(entityType, "salary1");
        assertNull(salary);
    }

    @Test
    public void shouldReturnClass() throws Exception {
        assertEquals(Employee.class, EntityTypeHelper.getClass(entityType));


    }
}
