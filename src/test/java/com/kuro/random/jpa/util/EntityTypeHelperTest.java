package com.kuro.random.jpa.util;

import com.kuro.random.jpa.testUtil.entity.Employee;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Kumar Rohit on 5/8/15.
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
}