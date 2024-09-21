package com.github.kuros.random.jpa.provider.mssql;

import com.github.kuros.random.jpa.provider.model.ForeignKeyRelation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
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
public class MSSQLRelationshipProviderTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Query query;
    private MSSQLRelationshipProvider mssqlRelationshipProvider;

    @BeforeEach public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mssqlRelationshipProvider = MSSQLRelationshipProvider.newInstance(entityManager);
    }

    @Test
    public void shouldReturnListOfRelationship() {
        mockEntityManager();
        final List<ForeignKeyRelation> foreignKeyRelations = mssqlRelationshipProvider.getForeignKeyRelations();
        assertEquals(1, foreignKeyRelations.size());
        final ForeignKeyRelation foreignKeyRelation = foreignKeyRelations.get(0);
        assertEquals("employee", foreignKeyRelation.getTable());
        assertEquals("person_id", foreignKeyRelation.getAttribute());
        assertEquals("person", foreignKeyRelation.getReferencedTable());
        assertEquals("id", foreignKeyRelation.getReferencedAttribute());
    }

    private void mockEntityManager() {
        final Object[] row1 = {"employee", "person_id", "person", "id"};

        final List<Object[]> resultList = new ArrayList<>();
        resultList.add(row1);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(resultList);
    }
}
