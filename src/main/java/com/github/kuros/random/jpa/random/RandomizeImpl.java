package com.github.kuros.random.jpa.random;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.types.FieldIndex;
import com.github.kuros.random.jpa.util.NumberUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
public final class RandomizeImpl implements Randomize {

    private final RandomGenerator randomGenerator;
    private final AttributeProvider attributeProvider;
    private Map<Field, Object> fieldValueMap;
    private Map<FieldIndex, Object> fieldIndexMap;

    private RandomizeImpl(final Cache cache, final RandomGenerator randomGenerator) {
        this.attributeProvider = cache.getAttributeProvider();
        this.randomGenerator = randomGenerator;
        this.fieldValueMap = new HashMap<Field, Object>();
        this.fieldIndexMap = new HashMap<FieldIndex, Object>();
    }

    public static RandomizeImpl newInstance(final Cache cache, final RandomGenerator randomGenerator) {
        return new RandomizeImpl(cache, randomGenerator);
    }

    public <T> T createRandom(final Class<T> type) {
        return randomGenerator.generateRandom(type);
    }

    public <T> T populateRandomFields(final T t, final int index) {
        final Class<?> type = t.getClass();
        final Field[] declaredFields = type.getDeclaredFields();
        for (final Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                if (isFieldValueProvided(declaredField, index)) {
                    declaredField.set(t, getFieldValue(declaredField, index));
                } else if (isRandomRequired(declaredField)) {
                    declaredField.set(t, NumberUtil.castNumber(declaredField.getType(), randomGenerator.generateRandom(declaredField)));
                }
            } catch (final Exception e) {
                throw new RandomJPAException("Try adding RandomClassGenerator/RandomAttributeGenerator, Unable to set random value for "
                        + declaredField.getDeclaringClass() + " - " + declaredField.getName(), e);
            }
        }

        return t;
    }

    private Object getFieldValue(final Field declaredField, final int index) {
        final Object value = fieldIndexMap.get(new FieldIndex(declaredField, index));
        if (value == null) {
            return fieldValueMap.get(declaredField);
        }
        return value;
    }

    private boolean isFieldValueProvided(final Field declaredField, final int index) {
        return fieldIndexMap.containsKey(new FieldIndex(declaredField, index)) || fieldValueMap.containsKey(declaredField);
    }

    public boolean isValueProvided(final Field field, final int index) {
        return getFieldValue(field, index) != null;
    }

    public void addFieldValue(final Map<Field, Object> fieldValues) {
        this.fieldValueMap = fieldValues;
    }

    public void addFieldValue(final Field field, final Object value) {
        fieldValueMap.put(field, value);
    }

    public void addFieldValue(final Field field, final int index, final Object value) {
        fieldIndexMap.put(new FieldIndex(field, index), value);
    }

    private boolean isRandomRequired(final Field declaredField) {
        final EntityTableMapping entityTableMapping = attributeProvider.get(declaredField.getDeclaringClass());

        return (fieldIsColumn(entityTableMapping, declaredField) && !fieldIsId(entityTableMapping, declaredField))
                || randomGenerator.isRandomAttributeGeneratorProvided(declaredField);

    }

    private boolean fieldIsColumn(final EntityTableMapping entityTableMapping, final Field field) {
        return entityTableMapping != null && entityTableMapping.getAttributeNames().contains(field.getName());
    }

    private boolean fieldIsId(final EntityTableMapping entityTableMapping, final Field field) {
        return entityTableMapping != null && entityTableMapping.getAttributeIds().contains(field.getName());
    }
}
