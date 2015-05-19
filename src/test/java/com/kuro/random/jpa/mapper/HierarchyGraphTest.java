package com.kuro.random.jpa.mapper;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/3/15.
 */
public class HierarchyGraphTest {

    /**
     * TestClass        TestClass2
     * attr1     --->   attr1
     * @throws NoSuchFieldException
     */
    @Test
    public void createHierarchyGraphWithSingleFieldRelation() throws NoSuchFieldException {
        final Class<TestClass> testClass1 = TestClass.class;
        final Field personId = testClass1.getDeclaredField("attr1");

        final FieldValue from = FieldValue.newInstance(personId);

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field id = testClass2.getDeclaredField("attr1");

        final FieldValue to = FieldValue.newInstance(id);

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        final Relation relation = Relation.newInstance(from, to);
        hierarchyGraph.addRelation(relation);
        final Set<Class<?>> parent = hierarchyGraph.getParents(testClass1);
        Assert.assertEquals(1, parent.size());
        Assert.assertTrue(parent.contains(TestClass2.class));

        final TableNode tableNode = hierarchyGraph.getTableNode(TestClass.class);
        final List<Relation<?, ?>> relations = tableNode.getRelations();
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(from.getField(), relations.get(0).getFrom().getField());
        Assert.assertEquals(to.getField(), relations.get(0).getTo().getField());


        final TableNode actual = hierarchyGraph.getTableNode(TestClass2.class);
        Assert.assertEquals(0, actual.getParentClasses().size());
        Assert.assertEquals(0, actual.getRelations().size());
    }

    /**
     * TestClass        TestClass2
     * attr1     --->   attr1
     * attr2     --->   attr2
     * @throws NoSuchFieldException
     */
    @Test
    public void createHierarchyGraphWithMultipleFieldRelation() throws NoSuchFieldException {
        final Class<TestClass> testClass1 = TestClass.class;
        final Field attr11 = testClass1.getDeclaredField("attr1");
        final FieldValue from1 = FieldValue.newInstance(attr11);
        final Field attr12 = testClass1.getDeclaredField("attr2");
        final FieldValue from2 = FieldValue.newInstance(attr12);

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field attr21 = testClass2.getDeclaredField("attr1");
        final FieldValue to1 = FieldValue.newInstance(attr21);
        final Field attr22 = testClass2.getDeclaredField("attr2");
        final FieldValue to2 = FieldValue.newInstance(attr22);

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();
        hierarchyGraph.addRelation(Relation.newInstance(from1, to1));
        hierarchyGraph.addRelation(Relation.newInstance(from2, to2));

        final Set<Class<?>> children = hierarchyGraph.getParents(testClass1);

        Assert.assertEquals(1, children.size());
        final TableNode tableNode = hierarchyGraph.getTableNode(testClass1);

        Assert.assertTrue(tableNode.getParentClasses().contains(TestClass2.class));
        final List<Relation<?, ?>> relations = tableNode.getRelations();
        Assert.assertEquals(2, relations.size());

        final List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("attr1");
        fieldNames.add("attr2");

        Assert.assertEquals("attr1", relations.get(0).getFrom().getField().getName());
        Assert.assertEquals("attr1", relations.get(0).getTo().getField().getName());

        Assert.assertEquals("attr2", relations.get(1).getFrom().getField().getName());
        Assert.assertEquals("attr2", relations.get(1).getTo().getField().getName());
    }

    /**
     * TestClass        TestClass2              TestClass3
     * attr1     --->   attr1
     *                  attr2         --->      attr3
     * @throws NoSuchFieldException
     */
    @Test
    public void createHierarchyGraphWithMultipleChainedHierarchy() throws NoSuchFieldException {
        final Class<TestClass> testClass1 = TestClass.class;
        final Field attr11 = testClass1.getDeclaredField("attr1");
        final FieldValue from1 = FieldValue.newInstance(attr11);

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field attr21 = testClass2.getDeclaredField("attr1");
        final FieldValue to1 = FieldValue.newInstance(attr21);
        final Field attr22 = testClass2.getDeclaredField("attr2");
        final FieldValue from2 = FieldValue.newInstance(attr22);

        final Class<TestClass3> testClass3 = TestClass3.class;
        final Field attr3 = testClass3.getDeclaredField("attr3");
        final FieldValue to2 = FieldValue.newInstance(attr3);

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();
        hierarchyGraph.addRelation(Relation.newInstance(from1, to1));
        hierarchyGraph.addRelation(Relation.newInstance(from2, to2));


        final Set<Class<?>> parents = hierarchyGraph.getParents(testClass1);
        Assert.assertEquals(1, parents.size());

        final Set<Class<?>> parents1 = hierarchyGraph.getParents(testClass2);
        Assert.assertEquals(1, parents1.size());

        final Set<Class<?>> parents2 = hierarchyGraph.getParents(testClass3);
        Assert.assertEquals(0, parents2.size());
    }

    private class TestClass {
        private Integer attr1;
        private Integer attr2;
    }

    private class TestClass2 {
        private Integer attr1;
        private Integer attr2;
    }

    private class TestClass3 {
        private Integer attr3;
    }
}
