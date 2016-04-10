package com.github.kuros.random.jpa.random.generator;

import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.random.generator.types.BigDecimalGenerator;
import com.github.kuros.random.jpa.random.generator.types.BigIntegerGenerator;
import com.github.kuros.random.jpa.random.generator.types.BlobGenerator;
import com.github.kuros.random.jpa.random.generator.types.BooleanGenerator;
import com.github.kuros.random.jpa.random.generator.types.ByteGenerator;
import com.github.kuros.random.jpa.random.generator.types.CharacterGenerator;
import com.github.kuros.random.jpa.random.generator.types.ClobGenerator;
import com.github.kuros.random.jpa.random.generator.types.DateGenerator;
import com.github.kuros.random.jpa.random.generator.types.DoubleGenerator;
import com.github.kuros.random.jpa.random.generator.types.FloatGenerator;
import com.github.kuros.random.jpa.random.generator.types.IntegerGenerator;
import com.github.kuros.random.jpa.random.generator.types.LongGenerator;
import com.github.kuros.random.jpa.random.generator.types.ShortGenerator;
import com.github.kuros.random.jpa.random.generator.types.StringGenerator;
import com.github.kuros.random.jpa.util.NumberUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
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
public class RandomFactory {
    private static final Logger LOGGER = LogFactory.getLogger(RandomFactory.class);
    private Map<Class<?>, RandomClassGenerator> randomClassGeneratorMap;
    private static final Random RANDOM = new Random();

    public RandomFactory() {
        randomClassGeneratorMap = new HashMap<Class<?>, RandomClassGenerator>();
        init();
    }

    public RandomFactory addRandomGenerator(final RandomClassGenerator randomClassGenerator) {
        for (Class<?> aClass : randomClassGenerator.getTypes()) {
            randomClassGeneratorMap.put(aClass, randomClassGenerator);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T generateRandom(final Class<T> type) {
        final RandomClassGenerator generator = randomClassGeneratorMap.get(type);
        if (generator != null) {
            return (T) generator.doGenerate(type);
        }

        if (type.isEnum()) {
            final T[] enumConstants = type.getEnumConstants();
            return enumConstants[RANDOM.nextInt(enumConstants.length)];
        }

        T t;
        try {
            t = type.newInstance();
        } catch (final Exception e) {
            try {
                final Constructor<?> constructor = getConstructor(type);
                constructor.setAccessible(true);
                final Class<?>[] parameterTypes = constructor.getParameterTypes();
                final Object[] initArgs = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    initArgs[i] = generateRandom(parameterTypes[i]);
                }
                t = (T) constructor.newInstance(initArgs);
                setNullValues(t);
            } catch (final Exception e1) {
                t = null;
            }
        }

        return t;
    }

    private <T> void setNullValues(final T t) {
        final Class<?> type = t.getClass();
        final Field[] declaredFields = type.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            setFieldRandomValue(declaredField, t, NumberUtil.getDefaultValue(declaredField.getType()));
        }
    }

    private <T> void setFieldRandomValue(final Field field, final T randomObject, final Object value) {
        try {
            final int mod = field.getModifiers();
            if ((mod & Modifier.FINAL) == 0 && (mod & Modifier.STATIC) == 0) {
                field.setAccessible(true);
                field.set(randomObject, value);
            }
        } catch (final IllegalAccessException e) {
            LOGGER.debug("Unable to access field: " + field);
        }
    }

    private <T> Constructor<?> getConstructor(final Class<T> type) {
        final Constructor<?>[] constructors = type.getDeclaredConstructors();
        Constructor value = constructors[0];
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length < value.getParameterTypes().length) {
                value = constructor;
            }
        }
        return value;
    }

    private void init() {
        addRandomGenerator(BigDecimalGenerator.getInstance());
        addRandomGenerator(BigIntegerGenerator.getInstance());
        addRandomGenerator(BlobGenerator.getInstance());
        addRandomGenerator(BooleanGenerator.getInstance());
        addRandomGenerator(ByteGenerator.getInstance());
        addRandomGenerator(CharacterGenerator.getInstance());
        addRandomGenerator(ClobGenerator.getInstance());
        addRandomGenerator(DateGenerator.getInstance());
        addRandomGenerator(DoubleGenerator.getInstance());
        addRandomGenerator(FloatGenerator.getInstance());
        addRandomGenerator(IntegerGenerator.getInstance());
        addRandomGenerator(LongGenerator.getInstance());
        addRandomGenerator(ShortGenerator.getInstance());
        addRandomGenerator(StringGenerator.getInstance());
    }
}
