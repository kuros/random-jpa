package com.github.kuros.random.jpa.random;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.util.NumberUtil;

import java.lang.reflect.Field;

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
    private final AttributeProvider attributProvider;

    private RandomizeImpl(final RandomGenerator randomGenerator) {
        this.attributProvider = AttributeProvider.getInstance();
        this.randomGenerator = randomGenerator;
    }

    public static Randomize newInstance(final RandomGenerator randomGenerator) {
        return new RandomizeImpl(randomGenerator);
    }

    public <T> T createRandom(final Class<T> type) {
        final T t = randomGenerator.generateRandom(type);

        final Field[] declaredFields = type.getDeclaredFields();
        for (final Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                if (isRandomRequired(declaredField)) {
                    declaredField.set(t, NumberUtil.castNumber(declaredField.getType(), randomGenerator.generateRandom(declaredField)));
                }
            } catch (final Exception e) {
                throw new RandomJPAException("Try adding RandomClassGenerator/RandomAttributeGenerator, Unable to set random value for "
                        + declaredField.getDeclaringClass() + " - " + declaredField.getName() , e);
            }
        }
        return t;
    }

    private boolean isRandomRequired(final Field declaredField) {

        if (randomGenerator.isValueProvided(declaredField)) {
            return true;
        }

        final EntityTableMapping entityTableMapping = attributProvider.get(declaredField.getDeclaringClass());

        return !(entityTableMapping == null || fieldIsNotColumn(entityTableMapping, declaredField))
                && (!entityTableMapping.getAttributeIds().contains(declaredField.getName())
                || attributProvider.getUnSupportedGeneratorType().contains(entityTableMapping.getIdentifierGenerator()));

    }

    private boolean fieldIsNotColumn(final EntityTableMapping entityTableMapping, final Field field) {

        return !entityTableMapping.getAttributeNames().contains(field.getName());
    }
}
