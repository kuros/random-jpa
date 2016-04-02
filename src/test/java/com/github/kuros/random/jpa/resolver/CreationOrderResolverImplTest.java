package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.link.Preconditions;
import com.github.kuros.random.jpa.testUtil.entity.F;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.MockedHierarchyGraph;
import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.C;
import com.github.kuros.random.jpa.testUtil.entity.D;
import com.github.kuros.random.jpa.testUtil.entity.E;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.types.ClassDepth;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.types.Version;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreationOrderResolverImplTest {

    @Mock
    private Cache cache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cache.getVersion()).thenReturn(Version.V2);
    }

    @Test
    public void shouldGenerateOrderForZeroDepth() throws Exception {
        final CreationOrderResolver resolver = CreationOrderResolverImpl.newInstance(cache, MockedHierarchyGraph.getHierarchyGraph());

        final CreationOrder creationOrder = resolver.getCreationOrder(Entity.of(A.class));
        assertNotNull(creationOrder);

        final List<ClassDepth<?>> order = creationOrder.getOrder();
        assertEquals(1, order.size());

        final ClassDepth<?> classDepth = order.get(0);
        assertEquals(A.class, classDepth.getType());
        assertEquals(0, classDepth.getDepth());

        final Integer creationCount = creationOrder.getCreationCount().get(A.class);
        assertEquals(1, creationCount.intValue());
    }

    @Deprecated
    @Test
    public void shouldGenerateOrderForMultipleDepthV1() throws Exception {
        Mockito.when(cache.getVersion()).thenReturn(Version.V1);
        final Preconditions preconditions = new Preconditions();
        final CreationOrderResolver resolver = CreationOrderResolverImpl.newInstance(cache, MockedHierarchyGraph.getHierarchyGraph(), preconditions);

        final CreationOrder creationOrder = resolver.getCreationOrder(Entity.of(A.class), Entity.of(E.class));
        assertNotNull(creationOrder);

        final List<ClassDepth<?>> order = creationOrder.getOrder();
        assertEquals(6, order.size());

        verify(order);
    }

    @Deprecated
    @Test
    public void shouldGenerateOrderForMultipleDepthV1WithPreConditions() throws Exception {
        Mockito.when(cache.getVersion()).thenReturn(Version.V1);
        final Preconditions preconditions = new Preconditions();
        preconditions.add(A.class, Plan.of(Entity.of(X.class)));
        final CreationOrderResolver resolver = CreationOrderResolverImpl.newInstance(cache, MockedHierarchyGraph.getHierarchyGraph(), preconditions);

        final CreationOrder creationOrder = resolver.getCreationOrder(Entity.of(A.class), Entity.of(E.class));
        assertNotNull(creationOrder);

        final List<ClassDepth<?>> order = creationOrder.getOrder();
        assertEquals(7, order.size());

        verify(order);
    }

    @Test
    public void shouldGenerateOrderForMultipleDepthV2() throws Exception {
        final CreationOrderResolver resolver = CreationOrderResolverImpl.newInstance(cache, MockedHierarchyGraph.getHierarchyGraph());

        final CreationOrder creationOrder = resolver.getCreationOrder(Entity.of(A.class), Entity.of(E.class));
        assertNotNull(creationOrder);

        final List<ClassDepth<?>> order = creationOrder.getOrder();
        assertEquals(6, order.size());

        verify(order);
    }

    private void verify(final List<ClassDepth<?>> order) {
        final Map<Class<?>, Integer> expectedDepths = getExpectedDepths();
        for (ClassDepth<?> classDepth : order) {
            assertEquals(expectedDepths.get(classDepth.getType()).intValue(), classDepth.getDepth());
        }
    }

    private Map<Class<?>, Integer> getExpectedDepths() {
        final Map<Class<?>, Integer> expected = new HashMap<Class<?>, Integer>();
        expected.put(X.class, 0);
        expected.put(A.class, 3);
        expected.put(B.class, 3);
        expected.put(C.class, 2);
        expected.put(D.class, 1);
        expected.put(F.class, 1);
        expected.put(E.class, 0);
        return expected;
    }
}
