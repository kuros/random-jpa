package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class CyclicValidatorTest {

    @Test
    public void testSelfCyclicDependency() {
        assertThrows(RandomJPAException.class, () -> {

            final Field table1 = TestClass.class.getDeclaredField("attr1");

            final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();
            hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table1), new FieldWrapper(table1)));
            hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table1), new FieldWrapper(table1)));

            final CyclicValidator cyclicValidator = new CyclicValidator(hierarchyGraph);
            cyclicValidator.validate();
        });
    }

    @Test
    public void testDirectCyclicDependency() {
        assertThrows(RandomJPAException.class, () -> {

            final Field table1 = TestClass.class.getDeclaredField("attr1");
            final Field table2 = TestClass2.class.getDeclaredField("attr1");

            final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();
            hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table1), new FieldWrapper(table2)));
            hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table2), new FieldWrapper(table1)));

            final CyclicValidator cyclicValidator = new CyclicValidator(hierarchyGraph);
            cyclicValidator.validate();
        });
    }

    @Test
    public void testInDirectCyclicDependency() {
        assertThrows(RandomJPAException.class, () -> {

            final Field table1 = TestClass.class.getDeclaredField("attr1");
            final Field table2 = TestClass2.class.getDeclaredField("attr1");
            final Field table3 = TestClass3.class.getDeclaredField("attr3");

            final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();
            hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table1), new FieldWrapper(table2)));
            hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table2), new FieldWrapper(table3)));
            hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table3), new FieldWrapper(table1)));

            final CyclicValidator cyclicValidator = new CyclicValidator(hierarchyGraph);
            cyclicValidator.validate();
        });
    }

    @Test
    public void shouldNotThrowExceptionWhenNoDependencyFound() throws Exception {
        final Field table1 = TestClass.class.getDeclaredField("attr1");
        final Field table2 = TestClass2.class.getDeclaredField("attr1");
        final Field table3 = TestClass3.class.getDeclaredField("attr3");

        final HierarchyGraph hierarchyGraph = HierarchyGraph.newInstance();
        hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table1), new FieldWrapper(table2)));
        hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table2), new FieldWrapper(table3)));
        hierarchyGraph.addRelation(Relation.newInstance(new FieldWrapper(table1), new FieldWrapper(table3)));

        final CyclicValidator cyclicValidator = new CyclicValidator(hierarchyGraph);
        cyclicValidator.validate();
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
