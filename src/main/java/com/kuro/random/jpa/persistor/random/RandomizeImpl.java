package com.kuro.random.jpa.persistor.random;

import com.openpojo.random.RandomFactory;

import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public final class RandomizeImpl implements Randomize {

    private final RandomFactory randomFactory;

    private RandomizeImpl(final RandomFactory randomFactory) {
        this.randomFactory = randomFactory;
    }

    public static Randomize newInstance(final RandomFactory randomFactory) {
        return new RandomizeImpl(randomFactory);
    }

    public <T> T createRandom(final Class<T> type){
        final T t = randomFactory.getRandomValue(type);
        final Field[] declaredFields = type.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            final Class<?> fieldType = declaredField.getType();
            try {
                declaredField.set(t, randomFactory.getRandomValue(fieldType));
            } catch (final Exception e) {
            }
        }
        return t;
    }
}
