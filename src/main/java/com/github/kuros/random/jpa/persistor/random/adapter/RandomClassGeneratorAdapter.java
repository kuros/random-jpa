package com.github.kuros.random.jpa.persistor.random.adapter;

import com.github.kuros.random.jpa.persistor.random.generator.RandomClassGenerator;
import com.openpojo.random.RandomGenerator;

import java.util.Collection;

/**
 * Created by Kumar Rohit on 5/16/15.
 */
public class RandomClassGeneratorAdapter {

    public static RandomGenerator adapt(final RandomClassGenerator randomClassGenerator) {
        return new RandomGenerator() {
            public Collection<Class<?>> getTypes() {
                return randomClassGenerator.getTypes();
            }

            public Object doGenerate(final Class<?> aClass) {
                return randomClassGenerator.doGenerate(aClass);
            }
        };
    }
}
