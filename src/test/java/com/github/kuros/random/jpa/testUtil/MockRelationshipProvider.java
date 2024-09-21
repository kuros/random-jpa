package com.github.kuros.random.jpa.testUtil;

import org.mockito.Mockito;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;

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

        final List<Object> objects = new ArrayList<>();
        objects.add(relation1);
        objects.add(relation2);
        objects.add(relation3);
        objects.add(relation4);

        final Query query = Mockito.mock(Query.class);
        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(objects);
    }
}
