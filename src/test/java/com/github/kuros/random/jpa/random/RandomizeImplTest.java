package com.github.kuros.random.jpa.random;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
public class RandomizeImplTest {

    @Mock
    private Cache cache;

    @Mock
    private AttributeProvider attributeProvider;

    @Mock
    private RandomGenerator randomGenerator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cache.getAttributeProvider()).thenReturn(attributeProvider);
    }

    private void mockRandomGenerator() {
        Mockito.when(randomGenerator.generateRandom(Mockito.any(Field.class))).thenAnswer(new Answer<Object>() {
            public Object answer(final InvocationOnMock invocationOnMock) {
                final Field field = (Field) invocationOnMock.getArguments()[0];
                return RandomFixture.create(field.getType());
            }
        });
    }

    @Test
    public void shouldRandomizeClass() throws InstantiationException, IllegalAccessException {

        mockRandomGenerator();

        final ArgumentCaptor<Class> classArgumentCaptor = ArgumentCaptor.forClass(Class.class);
        final Randomize randomize = RandomizeImpl.newInstance(cache, randomGenerator);
        randomize.createRandom(RandomizeImplTestClass.class);
        Mockito.verify(randomGenerator, Mockito.times(1)).generateRandom(classArgumentCaptor.capture());

        assertEquals(RandomizeImplTestClass.class, classArgumentCaptor.getValue());
    }

    @Test
    public void shouldRandomizeFields() throws Exception {

        mockRandomGenerator();
        getMockedEntityTableMapping();

        final Randomize randomize = RandomizeImpl.newInstance(cache, randomGenerator);

        final RandomizeImplTestClass testClass = new RandomizeImplTestClass();

        final RandomizeImplTestClass actual = randomize.populateRandomFields(testClass);

        final ArgumentCaptor<Field> fieldArgumentCaptor = ArgumentCaptor.forClass(Field.class);

        Mockito.verify(randomGenerator, Mockito.times(3)).generateRandom(fieldArgumentCaptor.capture());

        final List<Field> allValues = fieldArgumentCaptor.getAllValues();
        assertEquals(3, allValues.size());
        assertEquals("aLongColumn", allValues.get(0).getName());
        assertEquals("aStringColumn", allValues.get(1).getName());
        assertEquals("aBooleanColumn", allValues.get(2).getName());

    }

    @Test
    public void shouldNotRandomizeFieldsWhenTheValuesHasBeenSetManually() throws Exception {

        mockRandomGenerator();
        getMockedEntityTableMapping();

        final Map<Field, Object> fieldObjectMap = new HashMap<Field, Object>();
        final Field aLongColumn = Util.getField(RandomizeImplTestClass.class, "aLongColumn");
        fieldObjectMap.put(aLongColumn, RandomFixture.create(aLongColumn.getType()));

        final Field aStringColumn = Util.getField(RandomizeImplTestClass.class, "aStringColumn");
        fieldObjectMap.put(aStringColumn, RandomFixture.create(aStringColumn.getType()));

        final Field aBooleanColumn = Util.getField(RandomizeImplTestClass.class, "aBooleanColumn");
        fieldObjectMap.put(aBooleanColumn, RandomFixture.create(aBooleanColumn.getType()));

        final Randomize randomize = RandomizeImpl.newInstance(cache, randomGenerator);
        randomize.addFieldValue(fieldObjectMap);

        final RandomizeImplTestClass testClass = new RandomizeImplTestClass();

        final RandomizeImplTestClass actual = randomize.populateRandomFields(testClass);
        Mockito.verify(randomGenerator, Mockito.times(0)).generateRandom(Mockito.any(Field.class));

        assertTrue(randomize.isValueProvided(aLongColumn));
        assertEquals(fieldObjectMap.get(aLongColumn), actual.aLongColumn);
        assertTrue(randomize.isValueProvided(aStringColumn));
        assertEquals(fieldObjectMap.get(aStringColumn), actual.aStringColumn);
        assertTrue(randomize.isValueProvided(aBooleanColumn));
        assertEquals(fieldObjectMap.get(aBooleanColumn), actual.aBooleanColumn);

    }

    @Test
    public void shouldNotRandomizeFieldsWhenNullAttributeValuesAreProvided() throws Exception {

        mockRandomGenerator();
        getMockedEntityTableMapping();

        final RandomizeImpl randomize = RandomizeImpl.newInstance(cache, randomGenerator);

        final List<Field> nullValueFields = new ArrayList<Field>();
        nullValueFields.add(Util.getField(RandomizeImplTestClass.class, "aLongColumn"));
        nullValueFields.add(Util.getField(RandomizeImplTestClass.class, "aStringColumn"));
        nullValueFields.add(Util.getField(RandomizeImplTestClass.class, "aBooleanColumn"));
        randomize.setNullValueFields(nullValueFields);

        final RandomizeImplTestClass testClass = new RandomizeImplTestClass();
        final RandomizeImplTestClass actual = randomize.populateRandomFields(testClass);
        Mockito.verify(randomGenerator, Mockito.times(0)).generateRandom(Mockito.any(Field.class));

        assertNull(actual.aLongColumn);
        assertNull(actual.aStringColumn);
        assertNull(actual.aBooleanColumn);

    }

    @Test(expected = RandomJPAException.class)
    public void shouldThrowExceptionErrorIsFoundSettingTheFieldValue() throws Exception {
        getMockedEntityTableMapping();

        Mockito.when(randomGenerator.generateRandom(Mockito.any(Field.class))).thenThrow(new RuntimeException());

        final RandomizeImpl randomize = RandomizeImpl.newInstance(cache, randomGenerator);

        final RandomizeImplTestClass testClass = new RandomizeImplTestClass();
        randomize.populateRandomFields(testClass);

    }

    private EntityTableMapping getMockedEntityTableMapping() {
        final EntityTableMapping entityTableMapping = new EntityTableMapping(RandomizeImplTestClass.class);
        entityTableMapping.addAttributeIds("id");
        entityTableMapping.addAttributeColumnMapping("aLongColumn", "long_column");
        entityTableMapping.addAttributeColumnMapping("aStringColumn", "string_column");
        entityTableMapping.addAttributeColumnMapping("aBooleanColumn", "boolean_column");
        Mockito.when(attributeProvider.get(Mockito.eq(RandomizeImplTestClass.class))).thenReturn(entityTableMapping);
        return entityTableMapping;
    }

    private class RandomizeImplTestClass {
        private int id;

        private Long aLongColumn;

        private String aStringColumn;

        private Boolean aBooleanColumn;

        private Object noColumn;
    }
}
