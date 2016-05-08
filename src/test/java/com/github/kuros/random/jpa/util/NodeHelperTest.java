package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.C;
import com.github.kuros.random.jpa.types.ClassIndex;
import com.github.kuros.random.jpa.types.Node;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NodeHelperTest {


    @Test
    public void shouldCreateSimpleHierarchy() throws Exception {
        final Node nodeA = Node.newInstance(A.class, 0, 0);
        final Node nodeB = Node.newInstance(B.class, 0, 0);
        final Node nodeC = Node.newInstance(C.class, 0, 0);

        nodeB.setChildNodes(newArrayList(nodeC));
        nodeA.setChildNodes(newArrayList(nodeB));

        final List<ClassIndex> reverseOrder = NodeHelper.getClassIndexInOrder(nodeA, C.class, 0);

        assertEquals(C.class, reverseOrder.get(0).getType());
        assertEquals(0, reverseOrder.get(0).getIndex());
        assertEquals(B.class, reverseOrder.get(1).getType());
        assertEquals(0, reverseOrder.get(1).getIndex());
        assertEquals(A.class, reverseOrder.get(2).getType());
        assertEquals(0, reverseOrder.get(2).getIndex());
    }

    @Test
    public void shouldReturnEmptyListIfTypeIndexDoesNotMatch() throws Exception {
        final Node nodeA = Node.newInstance(A.class, 0, 0);
        final Node nodeB = Node.newInstance(B.class, 0, 0);
        final Node nodeC = Node.newInstance(C.class, 0, 0);

        nodeB.setChildNodes(newArrayList(nodeC));
        nodeA.setChildNodes(newArrayList(nodeB));

        final List<ClassIndex> reverseOrder = NodeHelper.getClassIndexInOrder(nodeA, C.class, 1);

        assertEquals(0, reverseOrder.size());
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldCreateComplexHierarchy() throws Exception {
        final Node node = createComplexHierarchy();
        final List<Class<?>> expectedClassOrder = newArrayList(C.class, B.class, A.class);

        //scenario-1
        List<ClassIndex> reverseOrder = NodeHelper.getClassIndexInOrder(node, C.class, 0);
        List<Integer> expectedIndexOrder = newArrayList(0, 0, 0);
        verify(expectedClassOrder, expectedIndexOrder, reverseOrder);

        //scenario-2
        reverseOrder = NodeHelper.getClassIndexInOrder(node, C.class, 1);
        expectedIndexOrder = newArrayList(1, 0, 0);
        verify(expectedClassOrder, expectedIndexOrder, reverseOrder);

        //scenario-3
        reverseOrder = NodeHelper.getClassIndexInOrder(node, C.class, 2);
        expectedIndexOrder = newArrayList(2, 1, 0);
        verify(expectedClassOrder, expectedIndexOrder, reverseOrder);

        //scenario-4
        reverseOrder = NodeHelper.getClassIndexInOrder(node, C.class, 3);
        expectedIndexOrder = newArrayList(3, 1, 0);
        verify(expectedClassOrder, expectedIndexOrder, reverseOrder);

        //scenario-4
        reverseOrder = NodeHelper.getClassIndexInOrder(node, C.class, 4);
        expectedIndexOrder = newArrayList(4, 1, 0);
        verify(expectedClassOrder, expectedIndexOrder, reverseOrder);
    }

    private void verify(final List<Class<?>> expectedClassOrder, final List<Integer> expectedIndexOrder, final List<ClassIndex> reverseOrder) {

        assertEquals(expectedClassOrder.size(), reverseOrder.size());
        assertEquals(expectedIndexOrder.size(), reverseOrder.size());

        for (int i = 0; i < reverseOrder.size(); i++) {
            assertEquals(expectedClassOrder.get(i), reverseOrder.get(i).getType());
            assertEquals(expectedIndexOrder.get(i).intValue(), reverseOrder.get(i).getIndex());
        }
    }

    private Node createComplexHierarchy() {
        final Node nodeA = Node.newInstance(A.class, 0, 0);

        final Node nodeB0 = Node.newInstance(B.class, 0, 0);
        final Node nodeB1 = Node.newInstance(B.class, 0, 1);

        final Node nodeC0 = Node.newInstance(C.class, 0, 0);
        final Node nodeC1 = Node.newInstance(C.class, 0, 1);
        final Node nodeC2 = Node.newInstance(C.class, 0, 2);
        final Node nodeC3 = Node.newInstance(C.class, 0, 3);
        final Node nodeC4 = Node.newInstance(C.class, 0, 4);

        nodeB0.setChildNodes(newArrayList(nodeC0, nodeC1));
        nodeB1.setChildNodes(newArrayList(nodeC2, nodeC3, nodeC4));
        nodeA.setChildNodes(newArrayList(nodeB0, nodeB1));
        return nodeA;
    }

    private <T> List<T> newArrayList(final T... values) {
        return Arrays.asList(values);
    }
}
