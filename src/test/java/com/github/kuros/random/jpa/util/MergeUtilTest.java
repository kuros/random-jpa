package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.C;
import com.github.kuros.random.jpa.testUtil.entity.D;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MergeUtilTest {

    @Test
    public void shouldNotMergeCreationOrdersWhenNoDuplicateEntryFound() {
        final CreationOrder creationOrder1 = CreationOrder.newInstance();
        creationOrder1.add(ClassDepth.newInstance(A.class, 0));
        creationOrder1.add(ClassDepth.newInstance(B.class, 0));

        final CreationOrder creationOrder2 = CreationOrder.newInstance();
        creationOrder2.add(ClassDepth.newInstance(C.class, 0));
        creationOrder2.add(ClassDepth.newInstance(D.class, 0));

        final List<CreationOrder> input = new ArrayList<>();
        input.add(creationOrder1);
        input.add(creationOrder2);

        final List<CreationOrder> actual = MergeUtil.merge(input);

        assertEquals(2, actual.size());
        assertTrue(actual.get(0).contains(ClassDepth.newInstance(A.class, 0)));
        assertTrue(actual.get(0).contains(ClassDepth.newInstance(B.class, 0)));

        assertTrue(actual.get(1).contains(ClassDepth.newInstance(C.class, 0)));
        assertTrue(actual.get(1).contains(ClassDepth.newInstance(D.class, 0)));
    }

    @Test
    public void shouldMergeCreationOrdersWhenDuplicateEntryFound() {
        final CreationOrder creationOrder1 = CreationOrder.newInstance();
        creationOrder1.add(ClassDepth.newInstance(A.class, 0));
        creationOrder1.add(ClassDepth.newInstance(B.class, 0));

        final CreationOrder creationOrder2 = CreationOrder.newInstance();
        creationOrder2.add(ClassDepth.newInstance(C.class, 0));
        creationOrder2.add(ClassDepth.newInstance(D.class, 0));

        final CreationOrder creationOrder3 = CreationOrder.newInstance();
        creationOrder3.add(ClassDepth.newInstance(B.class, 0));
        creationOrder3.add(ClassDepth.newInstance(C.class, 0));

        final List<CreationOrder> input = new ArrayList<>();
        input.add(creationOrder1);
        input.add(creationOrder2);
        input.add(creationOrder3);

        final List<CreationOrder> actual = MergeUtil.merge(input);

        assertEquals(1, actual.size());
        assertTrue(actual.get(0).contains(ClassDepth.newInstance(A.class, 0)));
        assertTrue(actual.get(0).contains(ClassDepth.newInstance(B.class, 0)));

        assertTrue(actual.get(0).contains(ClassDepth.newInstance(C.class, 0)));
        assertTrue(actual.get(0).contains(ClassDepth.newInstance(D.class, 0)));
    }

    @Test
    public void shouldMergeWhenDuplicateEntryFoundAndNotMergeOtherwise() {
        final CreationOrder creationOrder1 = CreationOrder.newInstance();
        creationOrder1.add(ClassDepth.newInstance(A.class, 0));
        creationOrder1.add(ClassDepth.newInstance(B.class, 0));

        final CreationOrder creationOrder2 = CreationOrder.newInstance();
        creationOrder2.add(ClassDepth.newInstance(C.class, 0));
        creationOrder2.add(ClassDepth.newInstance(D.class, 0));

        final CreationOrder creationOrder3 = CreationOrder.newInstance();
        creationOrder3.add(ClassDepth.newInstance(B.class, 0));
        creationOrder3.add(ClassDepth.newInstance(C.class, 0));

        final CreationOrder creationOrder4 = CreationOrder.newInstance();
        creationOrder4.add(ClassDepth.newInstance(X.class, 0));
        creationOrder4.add(ClassDepth.newInstance(Y.class, 0));

        final List<CreationOrder> input = new ArrayList<>();
        input.add(creationOrder1);
        input.add(creationOrder2);
        input.add(creationOrder3);
        input.add(creationOrder4);

        final List<CreationOrder> actual = MergeUtil.merge(input);

        assertEquals(2, actual.size());
        assertTrue(actual.get(0).contains(ClassDepth.newInstance(A.class, 0)));
        assertTrue(actual.get(0).contains(ClassDepth.newInstance(B.class, 0)));

        assertTrue(actual.get(0).contains(ClassDepth.newInstance(C.class, 0)));
        assertTrue(actual.get(0).contains(ClassDepth.newInstance(D.class, 0)));

        assertTrue(actual.get(1).contains(ClassDepth.newInstance(X.class, 0)));
        assertTrue(actual.get(1).contains(ClassDepth.newInstance(Y.class, 0)));
    }

    @Test
    public void shouldNotOverrideCreationCountIfLess() {
        final CreationOrder creationOrder1 = CreationOrder.newInstance();
        creationOrder1.add(ClassDepth.newInstance(A.class, 0));
        creationOrder1.add(ClassDepth.newInstance(B.class, 0));
        final int expectedACount = 3;
        creationOrder1.addCreationCount(A.class, expectedACount);
        final int expectedBCount = 2;
        creationOrder1.addCreationCount(B.class, expectedBCount);


        final CreationOrder creationOrder2 = CreationOrder.newInstance();
        creationOrder2.add(ClassDepth.newInstance(B.class, 0));
        creationOrder2.add(ClassDepth.newInstance(C.class, 0));

        final int expectedNewBCount = 4;
        creationOrder2.addCreationCount(B.class, expectedNewBCount);

        final int expectedCCount = 5;
        creationOrder2.addCreationCount(C.class, expectedCCount);

        final List<CreationOrder> input = new ArrayList<>();
        input.add(creationOrder1);
        input.add(creationOrder2);

        final List<CreationOrder> actual = MergeUtil.merge(input);

        assertEquals(1, actual.size());

        final CreationOrder creationOrder = actual.get(0);
        final Map<Class<?>, Integer> creationCountMap = creationOrder.getCreationCount();

        assertEquals(expectedACount, creationCountMap.get(A.class).intValue());
        assertEquals(expectedCCount, creationCountMap.get(C.class).intValue());
        assertEquals(expectedBCount, creationCountMap.get(B.class).intValue(), "Class B should creation count should be overridden by later");
    }

    @Test
    public void shouldModifyDepthIfMergerDepthIsHigh() {
        final CreationOrder creationOrder1 = CreationOrder.newInstance();
        creationOrder1.add(ClassDepth.newInstance(A.class, 1));
        creationOrder1.add(ClassDepth.newInstance(B.class, 2));

        final CreationOrder creationOrder2 = CreationOrder.newInstance();
        creationOrder2.add(ClassDepth.newInstance(B.class, 3));
        creationOrder2.add(ClassDepth.newInstance(C.class, 4));

        final List<CreationOrder> input = new ArrayList<>();
        input.add(creationOrder1);
        input.add(creationOrder2);

        final List<CreationOrder> actual = MergeUtil.merge(input);

        assertEquals(1, actual.size());

        final CreationOrder creationOrder = actual.get(0);
        final List<ClassDepth<?>> order = creationOrder.getOrder();
        assertEquals(3, order.size());
        order.sort(Comparator.comparing(o -> o.getType().getName()));

        assertEquals(A.class, order.get(0).getType());
        assertEquals(1, order.get(0).getDepth());

        assertEquals(B.class, order.get(1).getType());
        assertEquals(3, order.get(1).getDepth());

        assertEquals(C.class, order.get(2).getType());
        assertEquals(4, order.get(2).getDepth());
    }

    @Test
    public void shouldNotModifyDepthIfMergerDepthIsLow() {
        final CreationOrder creationOrder1 = CreationOrder.newInstance();
        creationOrder1.add(ClassDepth.newInstance(A.class, 1));
        creationOrder1.add(ClassDepth.newInstance(B.class, 3));

        final CreationOrder creationOrder2 = CreationOrder.newInstance();
        creationOrder2.add(ClassDepth.newInstance(B.class, 2));
        creationOrder2.add(ClassDepth.newInstance(C.class, 4));

        final List<CreationOrder> input = new ArrayList<>();
        input.add(creationOrder1);
        input.add(creationOrder2);

        final List<CreationOrder> actual = MergeUtil.merge(input);

        assertEquals(1, actual.size());

        final CreationOrder creationOrder = actual.get(0);
        final List<ClassDepth<?>> order = creationOrder.getOrder();
        assertEquals(3, order.size());
        order.sort(Comparator.comparing(o -> o.getType().getName()));

        assertEquals(A.class, order.get(0).getType());
        assertEquals(1, order.get(0).getDepth());

        assertEquals(B.class, order.get(1).getType());
        assertEquals(3, order.get(1).getDepth());

        assertEquals(C.class, order.get(2).getType());
        assertEquals(4, order.get(2).getDepth());
    }
}
