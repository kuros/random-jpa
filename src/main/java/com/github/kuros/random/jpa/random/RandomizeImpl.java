package com.github.kuros.random.jpa.random;

import com.github.kuros.random.jpa.random.generator.RandomGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public final class RandomizeImpl implements Randomize {

    private final RandomGenerator randomGenerator;

    private RandomizeImpl(final RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public static Randomize newInstance(final RandomGenerator randomGenerator) {
        return new RandomizeImpl(randomGenerator);
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

        if (fieldIsNotColumn(declaredField)) {
            return false;
        }

        final Id annotation = declaredField.getAnnotation(Id.class);
        if (annotation == null) {
            return true;
        }

        final GeneratedValue generatedValue = declaredField.getAnnotation(GeneratedValue.class);
        if (generatedValue != null) {
            final GenerationType strategy = generatedValue.strategy();
            if (strategy == GenerationType.IDENTITY) {
                return false;
            }
        }

        return true;
    }

    private boolean fieldIsNotColumn(final Field field) {
        return field.getAnnotation(Column.class) == null;
    }
}
