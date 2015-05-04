package com.kuro.random.jpa.provider;

import com.kuro.random.jpa.testUtil.MockEntityManagerProvider;
import com.kuro.random.jpa.testUtil.entity.Department;
import com.kuro.random.jpa.testUtil.entity.Employee;
import com.kuro.random.jpa.testUtil.entity.EmployeeDepartment;
import com.kuro.random.jpa.testUtil.entity.Person;
import com.kuro.random.jpa.testUtil.entity.Shift;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Kumar Rohit on 4/27/15.
 */
public class MetaModelProviderTest {

    private MockEntityManagerProvider entityManagerProvider;
    private EntityManager entityManager;
    private MetaModelProvider metaModelProvider;

    @Before
    public void setUp() throws Exception {
        entityManagerProvider = MockEntityManagerProvider.createMockEntityManager();
        entityManager = entityManagerProvider.getEntityManager();
        metaModelProvider = MetaModelProvider.newInstance(entityManager);
    }

    @Test
    public void mapMetaModelsToTheirTableNames() {
        final Map<String, EntityType<?>> result = metaModelProvider.getMetaModelRelations();

        validate(entityManagerProvider.createEntityType(Employee.class), result.get("Employee"));
        validate(entityManagerProvider.createEntityType(Department.class), result.get("department"));
        validate(entityManagerProvider.createEntityType(EmployeeDepartment.class), result.get("employee_department"));
        validate(entityManagerProvider.createEntityType(Person.class), result.get("person"));
        validate(entityManagerProvider.createEntityType(Shift.class), result.get("shift"));
    }

    private void validate(final EntityType expected, final EntityType actual) {
        assertEquals(expected.getJavaType(), actual.getJavaType());
    }
}