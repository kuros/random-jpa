package com.github.kuros.random.jpa.testUtil;

import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 4/26/15.
 */
public class MockRelationshipProvider {

    public static void addMockRelationship(final EntityManager entityManager) {
        final MockRelationshipProvider mockRelationshipProvider = new MockRelationshipProvider();
        mockRelationshipProvider.addRelations(entityManager);
    }

    private void addRelations(final EntityManager entityManager) {
        final String[] relation1 = {"Employee", "personId", "person", "personId"};
        final String[] relation2 = {"employee_department", "employeeId", "Employee", "employeeId"};
        final String[] relation3 = {"employee_department", "departmentId", "department", "departmentId"};
        final String[] relation4 = {"employee_department", "shiftId", "shift", "shiftId"};

        final List<Object> objects = new ArrayList<Object>();
        objects.add(relation1);
        objects.add(relation2);
        objects.add(relation3);
        objects.add(relation4);

        final Query query = Mockito.mock(Query.class);
        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(objects);
    }
}
