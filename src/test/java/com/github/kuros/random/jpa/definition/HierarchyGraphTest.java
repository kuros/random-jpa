package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
public class HierarchyGraphTest {

    /**
     * TestClass        TestClass2
     * attr1     --->   attr1
     * @throws NoSuchFieldException
     */
    @Test
    public void createHierarchyGraphWithSingleFieldRelation() throws NoSuchFieldException {
        final Class<TestClass> testClass1 = TestClass.class;
        final Field from = testClass1.getDeclaredField("attr1");

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field to = testClass2.getDeclaredField("attr1");

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        final Relation relation = Relation.newInstance(new FieldWrapper(from), new FieldWrapper(to));
        hierarchyGraph.addRelation(relation);
        final Set<Class<?>> parent = hierarchyGraph.getParents(testClass1);
        Assert.assertEquals(1, parent.size());
        Assert.assertTrue(parent.contains(TestClass2.class));

        final TableNode tableNode = hierarchyGraph.getTableNode(TestClass.class);
        final List<Relation> relations = tableNode.getRelations();
        Assert.assertEquals(1, relations.size());
        Assert.assertEquals(from, relations.get(0).getFrom().getField());
        Assert.assertEquals(to, relations.get(0).getTo().getField());


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
        final Field from1 = testClass1.getDeclaredField("attr1");
        final Field from2 = testClass1.getDeclaredField("attr2");

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field to1 = testClass2.getDeclaredField("attr1");
        final Field to2 = testClass2.getDeclaredField("attr2");

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();
        hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(from1), new FieldWrapper(to1)));
        hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(from2), new FieldWrapper(to2)));

        final Set<Class<?>> children = hierarchyGraph.getParents(testClass1);

        Assert.assertEquals(1, children.size());
        final TableNode tableNode = hierarchyGraph.getTableNode(testClass1);

        Assert.assertTrue(tableNode.getParentClasses().contains(TestClass2.class));
        final List<Relation> relations = tableNode.getRelations();
        Assert.assertEquals(2, relations.size());

        final List<String> fieldNames = new ArrayList<>();
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
        final Field from1 = testClass1.getDeclaredField("attr1");

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field to1 = testClass2.getDeclaredField("attr1");
        final Field from2 = testClass2.getDeclaredField("attr2");

        final Class<TestClass3> testClass3 = TestClass3.class;
        final Field to2 = testClass3.getDeclaredField("attr3");

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();
        hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(from1), new FieldWrapper(to1)));
        hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(from2), new FieldWrapper(to2)));


        final Set<Class<?>> parents = hierarchyGraph.getParents(testClass1);
        Assert.assertEquals(1, parents.size());

        final Set<Class<?>> parents1 = hierarchyGraph.getParents(testClass2);
        Assert.assertEquals(1, parents1.size());

        final Set<Class<?>> parents2 = hierarchyGraph.getParents(testClass3);
        Assert.assertEquals(0, parents2.size());
    }

    @Test
    public void shouldReturnKeySet() throws NoSuchFieldException {
        final Class<TestClass> testClass1 = TestClass.class;
        final Field from = testClass1.getDeclaredField("attr1");

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field to = testClass2.getDeclaredField("attr1");

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        final Relation relation = Relation.newInstance(new FieldWrapper(from), new FieldWrapper(to));
        hierarchyGraph.addRelation(relation);

        final Set<Class<?>> keySet = hierarchyGraph.getKeySet();
        Assert.assertEquals(2, keySet.size());
        Assert.assertTrue(keySet.contains(TestClass.class));
        Assert.assertTrue(keySet.contains(TestClass2.class));
    }

    @Test
    public void shouldGetParentRelations() throws NoSuchFieldException {
        final Class<TestClass> testClass1 = TestClass.class;
        final Field from = testClass1.getDeclaredField("attr1");

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field to = testClass2.getDeclaredField("attr1");

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        final Relation relation = Relation.newInstance(new FieldWrapper(from), new FieldWrapper(to));
        hierarchyGraph.addRelation(relation);

        final Set<Class<?>> keySet = hierarchyGraph.getParents(TestClass.class);
        Assert.assertEquals(1, keySet.size());
        Assert.assertTrue(keySet.contains(TestClass2.class));
    }

    @Test
    public void shouldGetAttributeRelations() throws NoSuchFieldException {
        final Class<TestClass> testClass1 = TestClass.class;
        final Field from = testClass1.getDeclaredField("attr1");

        final Class<TestClass2> testClass2 = TestClass2.class;
        final Field to = testClass2.getDeclaredField("attr1");

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();

        final Relation relation = Relation.newInstance(new FieldWrapper(from), new FieldWrapper(to));
        hierarchyGraph.addRelation(relation);

        Assert.assertNotNull(hierarchyGraph.getParentRelations());

        final Set<Relation> keySet = hierarchyGraph.getAttributeRelations(TestClass.class);
        Assert.assertEquals(1, keySet.size());

        final List<Relation> relations = new ArrayList<>(keySet);
        Assert.assertEquals(from, relations.get(0).getFrom().getField());
        Assert.assertEquals(to, relations.get(0).getTo().getField());
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
