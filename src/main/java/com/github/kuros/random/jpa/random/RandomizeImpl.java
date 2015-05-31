package com.github.kuros.random.jpa.random;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.EntityTableMapping;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;

import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public final class RandomizeImpl implements Randomize {

    private final RandomGenerator randomGenerator;
    private final AttributeProvider attributProvider;

    private RandomizeImpl(final AttributeProvider attributeProvider, final RandomGenerator randomGenerator) {
        this.attributProvider = attributeProvider;
        this.randomGenerator = randomGenerator;
    }

    public static Randomize newInstance(final AttributeProvider attributeProvider, final RandomGenerator randomGenerator) {
        return new RandomizeImpl(attributeProvider, randomGenerator);
    }

    public <T> T createRandom(final Class<T> type) {
        final T t = randomGenerator.generateRandom(type);

        final Field[] declaredFields = type.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                if (isRandomRequired(declaredField)) {
                    declaredField.set(t, randomGenerator.generateRandom(declaredField));
                }
            } catch (final Exception e) {
                //do nothing
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
