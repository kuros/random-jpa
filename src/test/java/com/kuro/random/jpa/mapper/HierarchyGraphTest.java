package com.kuro.random.jpa.mapper;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        hierarchyGraph.addRelation(from, to);
        final List<TableNode> children = hierarchyGraph.getParent(testClass1);

        Assert.assertEquals(1, children.size());
        final TableNode tableNode = children.get(0);
        Assert.assertEquals(TestClass2.class, tableNode.getTableClass());
        Assert.assertEquals(1, tableNode.getFieldValues().size());

        for (Object o : tableNode.getFieldValues()) {
            final FieldValue fieldValue = (FieldValue) o;
            Assert.assertEquals("attr1", fieldValue.getField().getName());
        }
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
        hierarchyGraph.addRelation(from1, to1);
        hierarchyGraph.addRelation(from2, to2);

        final List<TableNode> children = hierarchyGraph.getParent(testClass1);

        Assert.assertEquals(1, children.size());
        final TableNode tableNode = children.get(0);

        Assert.assertEquals(TestClass2.class, tableNode.getTableClass());
        Assert.assertEquals(2, tableNode.getFieldValues().size());

        final List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("attr1");
        fieldNames.add("attr2");

        for (Object o : tableNode.getFieldValues()) {
            final FieldValue fieldValue = (FieldValue) o;
            Assert.assertTrue(fieldNames.contains(fieldValue.getField().getName()));
        }
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
        hierarchyGraph.addRelation(from1, to1);
        hierarchyGraph.addRelation(from2, to2);


        final List<TableNode> children = hierarchyGraph.getParent(testClass1);
        Assert.assertEquals(1, children.size());
        final TableNode tableNode = children.get(0);

        Assert.assertEquals(TestClass2.class, tableNode.getTableClass());
        Assert.assertEquals(1, tableNode.getFieldValues().size());

        for (Object o : tableNode.getFieldValues()) {
            final FieldValue fieldValue = (FieldValue) o;
            Assert.assertEquals("attr1", fieldValue.getField().getName());
        }

        final List<TableNode> children2 = hierarchyGraph.getParent(testClass1);
        Assert.assertEquals(1, children2.size());
        final TableNode tableNode2 = children2.get(0);

        Assert.assertEquals(TestClass2.class, tableNode2.getTableClass());
        Assert.assertEquals(1, tableNode2.getFieldValues().size());

        for (Object o : tableNode2.getFieldValues()) {
            final FieldValue fieldValue = (FieldValue) o;
            Assert.assertEquals("attr1", fieldValue.getField().getName());
        }
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
