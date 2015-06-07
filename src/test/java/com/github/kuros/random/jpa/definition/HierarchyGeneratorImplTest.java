package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
public class HierarchyGeneratorImplTest {

    @Mock
    private HierarchyGraph hierarchyGraph;

    @Mock
    private Relation relation;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddRelation() throws Exception {
        HierarchyGeneratorImpl hierarchyGenerator = new HierarchyGeneratorImpl();
        List<Relation> relations = new ArrayList<Relation>();
        relations.add(relation);

        hierarchyGenerator.addRelation(hierarchyGraph, relations);
        ArgumentCaptor<Relation> argumentCaptor = ArgumentCaptor.forClass(Relation.class);
        verify(hierarchyGraph, times(1)).addRelation(argumentCaptor.capture());

        assertSame(relation, argumentCaptor.getValue());
    }
}
