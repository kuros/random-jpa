package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.C;
import com.github.kuros.random.jpa.testUtil.entity.D;
import com.github.kuros.random.jpa.testUtil.entity.E;
import com.github.kuros.random.jpa.testUtil.entity.F;
import com.github.kuros.random.jpa.testUtil.entity.P;
import com.github.kuros.random.jpa.testUtil.entity.Q;
import com.github.kuros.random.jpa.testUtil.entity.R;
import com.github.kuros.random.jpa.testUtil.entity.RelationEntity;
import com.github.kuros.random.jpa.testUtil.entity.RelationManyToOne;
import com.github.kuros.random.jpa.testUtil.entity.RelationOneToMany;
import com.github.kuros.random.jpa.testUtil.entity.RelationOneToOne;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.DependencyHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
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
public class HierarchyGeneratorImplTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddRelation() throws Exception {
        final List<Relation> relations = DependencyHelper.getDependency();
        final HierarchyGenerator hierarchyGenerator = new HierarchyGeneratorImpl();
        final HierarchyGraph hierarchyGraph = hierarchyGenerator.generate(relations);

        assertEquals(16, hierarchyGraph.getKeySet().size());
        assertEquals(0, hierarchyGraph.getParents(A.class).size());
        assertEquals(0, hierarchyGraph.getParents(B.class).size());
        assertEquals(0, hierarchyGraph.getParents(F.class).size());
        assertEquals(0, hierarchyGraph.getParents(X.class).size());
        assertEquals(0, hierarchyGraph.getParents(Y.class).size());

        assertEquals(2, hierarchyGraph.getParents(C.class).size());
        assertTrue(hierarchyGraph.getParents(C.class).contains(A.class));
        assertTrue(hierarchyGraph.getParents(C.class).contains(B.class));

        assertEquals(2, hierarchyGraph.getParents(D.class).size());
        assertTrue(hierarchyGraph.getParents(D.class).contains(A.class));
        assertTrue(hierarchyGraph.getParents(D.class).contains(C.class));

        assertEquals(3, hierarchyGraph.getParents(E.class).size());
        assertTrue(hierarchyGraph.getParents(E.class).contains(B.class));
        assertTrue(hierarchyGraph.getParents(E.class).contains(D.class));
        assertTrue(hierarchyGraph.getParents(E.class).contains(F.class));

        assertEquals(2, hierarchyGraph.getParents(Z.class).size());
        assertTrue(hierarchyGraph.getParents(Z.class).contains(X.class));
        assertTrue(hierarchyGraph.getParents(Z.class).contains(Y.class));

        assertEquals(1, hierarchyGraph.getParents(Q.class).size());
        assertTrue(hierarchyGraph.getParents(Q.class).contains(P.class));

        assertEquals(1, hierarchyGraph.getParents(R.class).size());
        assertTrue(hierarchyGraph.getParents(R.class).contains(P.class));

        assertEquals(2, hierarchyGraph.getParents(RelationEntity.class).size());
        assertTrue(hierarchyGraph.getParents(RelationEntity.class).contains(RelationOneToOne.class));
        assertTrue(hierarchyGraph.getParents(RelationEntity.class).contains(RelationManyToOne.class));

        assertEquals(1, hierarchyGraph.getParents(RelationOneToMany.class).size());
        assertTrue(hierarchyGraph.getParents(RelationOneToMany.class).contains(RelationEntity.class));
    }
}
