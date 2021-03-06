package com.github.kuros.random.jpa.random.simple;

import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.random.generator.RandomFactory;
import com.github.kuros.random.jpa.random.generator.RandomFieldGenerator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
public class SimpleRandomGenerator {

    private static final Logger LOGGER = LogFactory.getLogger(SimpleRandomGenerator.class);
    private final RandomFactory randomFactory;
    private final Map<Class<?>, RandomFieldGenerator> randomFieldGeneratorMap;
    private final Random random;
    private static final List<Class> SKIP_FIELD_GENERATION;

    SimpleRandomGenerator(final RandomFactory randomFactory,
                          final Map<Class<?>, RandomFieldGenerator> randomFieldGeneratorMap) {
        this.randomFactory = randomFactory;
        this.randomFieldGeneratorMap = randomFieldGeneratorMap;
        this.random = new Random();
    }

    public <T> T getRandom(final Class<T> type) {

        final T randomObject = randomFactory.generateRandom(type);

        if (!SKIP_FIELD_GENERATION.contains(type) && !type.isEnum()) {
            if (randomNotRequired(randomObject)) {
                return null;
            }

            final RandomFieldGenerator randomFieldGenerator = randomFieldGeneratorMap.get(type);

            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (randomFieldGenerator != null
                        && randomFieldGenerator.getFieldNames().contains(field.getName())) {
                    final Object randomValue = randomFieldGenerator.doGenerate(field.getName());
                    setFieldRandomValue(randomObject, field, randomValue);
                } else if (field.getType().isArray()) {
                    setArrays(randomObject, field);
                } else {
                    Object randomValue = randomFactory.generateRandom(field.getType());
                    if (randomNotRequired(randomValue)) {
                        randomValue = null;
                    }
                    setFieldRandomValue(randomObject, field, randomValue);
                }
            }
        }

        return randomObject;
    }

    private <T> void setArrays(final T randomObject, final Field field) {
        final int count = random.nextInt(5) + 1;
        final Object arrayInstance = Array.newInstance(field.getType().getComponentType(), count);

        for (int i = 0; i < count; i++) {
            Array.set(arrayInstance, i, getRandom(field.getType().getComponentType()));
        }
        setFieldRandomValue(randomObject, field, arrayInstance);
    }

    private boolean randomNotRequired(final Object randomObject) {
        return randomObject instanceof Collection || randomObject instanceof Map;
    }


    private <T> void setFieldRandomValue(final T randomObject, final Field field, final Object randomValue) {
        try {
            final int mod = field.getModifiers();
            if ((mod & Modifier.FINAL) == 0 && (mod & Modifier.STATIC) == 0) {
                field.set(randomObject, randomValue);
            }
        } catch (final IllegalAccessException e) {
            LOGGER.debug("Unable to access field: " + field);
        }
    }

    static {
        SKIP_FIELD_GENERATION = new ArrayList<Class>();
        SKIP_FIELD_GENERATION.add(String.class);
    }
}
