package com.github.kuros.random.jpa.random.simple;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;
import com.github.kuros.random.jpa.random.generator.RandomFieldGenerator;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
public class SimpleRandomGeneratorTest {

    @Test
    public void testRandomObjectIsCreatedWithSimpleAttributes() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance("com.github.kuros.random")
                .create();

        final TestClass1 random = simpleRandomGenerator.getRandom(TestClass1.class);
        assertNotNull(random);
        assertNotNull(random.i);
        assertNotNull(random.s);
        assertNotNull(random.d);
    }

    @Test
    public void testRandomObjectIsCreatedWithSimpleAttributesWhenNoPackageNameIsProvided() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestClass1 random = simpleRandomGenerator.getRandom(TestClass1.class);
        assertNotNull(random);
        assertNotNull(random.i);
        assertNotNull(random.s);
        assertNotNull(random.d);
    }

    @Test
    public void testRandomObjectIsCreatedForComplexObjectsButCustomAttributesIsNotGenerated() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestClass2 testClass2 = simpleRandomGenerator.getRandom(TestClass2.class);

        assertNotNull(testClass2);
        assertNotNull(testClass2.i);
        final TestClass1 testClass1 = testClass2.testClass1;
        assertNotNull(testClass1);
        assertNull(testClass1.i);
        assertNull(testClass1.s);
        assertNull(testClass1.d);

    }

    @Test
    public void testRandomObjectIsCreatedForComplexObjectsButCustomAttributesIsAlsoGenerated() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance("com.github.kuros")
                .create();

        final TestClass2 testClass2 = simpleRandomGenerator.getRandom(TestClass2.class);

        assertNotNull(testClass2);
        assertNotNull(testClass2.i);
        final TestClass1 testClass1 = testClass2.testClass1;
        assertNotNull(testClass1);
        assertNotNull(testClass1.i);
        assertNotNull(testClass1.s);
        assertNotNull(testClass1.d);

    }

    @Test
    public void testExplicitGenerationOfClass() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .with(new RandomClassGenerator() {
                    public Collection<Class<?>> getTypes() {
                        final Set<Class<?>> classes = new HashSet<Class<?>>();
                        classes.add(Integer.class);
                        return classes;
                    }

                    public Object doGenerate(final Class<?> aClass) {
                        return 1234;
                    }
                })
                .create();

        final TestClass1 random = simpleRandomGenerator.getRandom(TestClass1.class);
        assertNotNull(random);
        assertEquals(1234, random.i.intValue());
    }

    @Test
    public void testEnumIsGenerated() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestEnum random = simpleRandomGenerator.getRandom(TestEnum.class);
        assertNotNull(random);

        final TestEnumClass random2 = simpleRandomGenerator.getRandom(TestEnumClass.class);
        assertNotNull(random2);
        assertNotNull(random2.i);
        assertNotNull(random2.testEnum);
    }

    @Test
    public void testCollectionIsNotGenerated() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .create();

        final TestCollection random = simpleRandomGenerator.getRandom(TestCollection.class);
        assertNotNull(random);
        assertNull(random.list);
        assertNull(random.set);
        assertNull(random.map);
        assertNull(simpleRandomGenerator.getRandom(Map.class));
        assertNull(simpleRandomGenerator.getRandom(List.class));
        assertNull(simpleRandomGenerator.getRandom(Set.class));
    }

    @Test
    public void testArrayIsGenerated() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance("com.github.kuros")
                .create();

        final TestArray random = simpleRandomGenerator.getRandom(TestArray.class);
        assertNotNull(random.i);
        System.out.println(random.testClass1s.length);
    }

    @Test
    public void shouldGenerateRandomFieldAsProvidedInRandomGenerator() throws Exception {
        final SimpleRandomGenerator simpleRandomGenerator = SimpleRandomGeneratorFactory
                .newInstance()
                .with(new RandomFieldGenerator() {
                    public Class<?> getType() {
                        return TestClass1.class;
                    }

                    public Set<String> getFieldNames() {
                        final Set<String> fieldNames = new HashSet<String>();
                        fieldNames.add("i");
                        fieldNames.add("s");
                        return fieldNames;
                    }

                    public Object doGenerate(final String fieldName) {
                        return "i".equals(fieldName) ? 1234 : "randomString";
                    }
                })
                .create();

        final TestClass1 random = simpleRandomGenerator.getRandom(TestClass1.class);
        assertEquals(1234, random.i.intValue());
        assertEquals("randomString", random.s);

        final TestClass2 random2 = simpleRandomGenerator.getRandom(TestClass2.class);
        assertNotEquals(1234, random2.i.intValue());
    }

    @Test (expected = RandomJPAException.class)
    public void shouldThrowExceptionWhenFieldIsNotFound() throws Exception {
        SimpleRandomGeneratorFactory
                .newInstance()
                .with(new RandomFieldGenerator() {
                    public Class<?> getType() {
                        return TestClass1.class;
                    }

                    public Set<String> getFieldNames() {
                        final Set<String> fieldNames = new HashSet<String>();
                        fieldNames.add("fieldNameThatNotExists");
                        return fieldNames;
                    }

                    public Object doGenerate(final String fieldName) {
                        return null;
                    }
                })
                .create();
    }
    private class TestClass1 {
        private Integer i;
        private String s;
        private Date d;
    }

    private class TestClass2 {
        private Integer i;
        private TestClass1 testClass1;
    }

    private class TestEnumClass {
        private int i;
        private TestEnum testEnum;
    }

    private class TestArray {
        private int[] i;
        private TestClass1[] testClass1s;
        private TestEnum[] testEnums;
    }

    private enum TestEnum {
        VAL1,
        VAL2,
        VAL3
    }

    private class TestCollection {
        private List<TestClass1> list;
        private Set<Integer> set;
        private Map<Integer, String> map;
    }
}
