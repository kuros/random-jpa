package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.C;
import com.github.kuros.random.jpa.testUtil.entity.D;
import com.github.kuros.random.jpa.testUtil.entity.E;
import com.github.kuros.random.jpa.testUtil.entity.P;
import com.github.kuros.random.jpa.testUtil.entity.Q;
import com.github.kuros.random.jpa.testUtil.entity.R;
import com.github.kuros.random.jpa.testUtil.entity.RelationEntity;
import com.github.kuros.random.jpa.testUtil.entity.RelationManyToOne;
import com.github.kuros.random.jpa.testUtil.entity.RelationOneToOne;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.MockedHierarchyGraph;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ChildGraphTest {

    @Test
    public void shouldGenerateChildGraph() throws Exception {

        final HierarchyGraph hierarchyGraph = MockedHierarchyGraph.getHierarchyGraph();

        final ChildGraph childGraph = ChildGraph.newInstance(hierarchyGraph);

        assertEquals(11, childGraph.keySet().size());
        assertEquals(0, childGraph.getChilds(E.class).size());
        assertEquals(0, childGraph.getChilds(Z.class).size());

        assertEquals(2, childGraph.getChilds(A.class).size());
        assertTrue(childGraph.getChilds(A.class).contains(C.class));
        assertTrue(childGraph.getChilds(A.class).contains(D.class));

        assertEquals(2, childGraph.getChilds(B.class).size());
        assertTrue(childGraph.getChilds(B.class).contains(C.class));
        assertTrue(childGraph.getChilds(B.class).contains(E.class));

        assertEquals(1, childGraph.getChilds(C.class).size());
        assertTrue(childGraph.getChilds(C.class).contains(D.class));

        assertEquals(1, childGraph.getChilds(D.class).size());
        assertTrue(childGraph.getChilds(D.class).contains(E.class));


        assertEquals(1, childGraph.getChilds(X.class).size());
        assertTrue(childGraph.getChilds(X.class).contains(Z.class));


        assertEquals(1, childGraph.getChilds(Y.class).size());
        assertTrue(childGraph.getChilds(Y.class).contains(Z.class));

        assertEquals(2, childGraph.getChilds(P.class).size());
        assertTrue(childGraph.getChilds(P.class).contains(Q.class));
        assertTrue(childGraph.getChilds(P.class).contains(R.class));

        assertEquals(1, childGraph.getChilds(RelationOneToOne.class).size());
        assertTrue(childGraph.getChilds(RelationOneToOne.class).contains(RelationEntity.class));

        assertEquals(1, childGraph.getChilds(RelationManyToOne.class).size());
        assertTrue(childGraph.getChilds(RelationManyToOne.class).contains(RelationEntity.class));
    }

    @Test
    public void shouldReturnRelationWithChilds() throws Exception {
        final HierarchyGraph hierarchyGraph = MockedHierarchyGraph.getHierarchyGraph();

        final ChildGraph childGraph = ChildGraph.newInstance(hierarchyGraph);

        final Set<Relation> childRelations = childGraph.getChildRelations(X.class);

        assertEquals(1, childRelations.size());
        final List<Relation> relations = new ArrayList<Relation>(childRelations);
        final Relation relation = relations.get(0);
        assertEquals("id" , relation.getFrom().getFieldName());
        assertEquals("xId" , relation.getTo().getFieldName());
    }

    @Test
    public void shouldReturnNullIfChildNodeNotFound() throws Exception {
        final ChildGraph childGraph = ChildGraph.newInstance();
        assertNull(childGraph.getNode(X.class));

    }
}
