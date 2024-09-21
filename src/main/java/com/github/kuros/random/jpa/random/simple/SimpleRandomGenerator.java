package com.github.kuros.random.jpa.random.simple;

import com.github.kuros.random.jpa.log.LogFactory;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.random.generator.RandomFactory;
import com.github.kuros.random.jpa.random.generator.RandomFieldGenerator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
    private static final List<Class<?>> SKIP_FIELD_GENERATION;
    private static final int MAX_RANDOM_STRING_LENGTH = 32;

    private static final Set<Class<?>> TYPES = Set.of(
            boolean.class, Boolean.class,
            int.class, Integer.class,
            float.class, Float.class,
            double.class, Double.class,
            long.class, Long.class,
            short.class, Short.class,
            byte.class, Byte.class,
            char.class, Character.class,
            String.class,
            Date.class,
            LocalDate.class,
            LocalDateTime.class,
            Calendar.class,
            BigDecimal.class,
            BigInteger.class);

    private static final char[] CHARACTERS = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    SimpleRandomGenerator(final RandomFactory randomFactory,
                          final Map<Class<?>, RandomFieldGenerator> randomFieldGeneratorMap) {
        this.randomFactory = randomFactory;
        this.randomFieldGeneratorMap = randomFieldGeneratorMap;
        this.random = new Random();
    }

    public <T> T getRandom(final Class<T> type) {

        if (TYPES.contains(type)) {
            return (T) generateRandom(type);
        }

        final T randomObject = randomFactory.generateRandom(type);

        if (!SKIP_FIELD_GENERATION.contains(type) && !type.isEnum()) {
            if (randomNotRequired(randomObject)) {
                return null;
            }

            final RandomFieldGenerator randomFieldGenerator = randomFieldGeneratorMap.get(type);

            for (Field field : type.getDeclaredFields()) {
                // In JDK 17, setAccessible(true) requires special permissions or workaround
                if (!field.canAccess(randomObject)) {
                    field.setAccessible(true); // Might require JVM flags like --add-opens in some cases
                }

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

    private <T> Object generateRandom(final Class<T> type) {
        if (type == Boolean.class || type == boolean.class) {
            return random.nextBoolean();
        }

        if (type == Integer.class || type == int.class) {
            return random.nextInt();
        }

        if (type == BigInteger.class) {
            return BigInteger.valueOf(random.nextLong());
        }

        if (type == Float.class || type == float.class) {
            return random.nextFloat();
        }

        if (type == Double.class || type == double.class) {
            return random.nextDouble();
        }

        if (type == BigDecimal.class) {
            return BigDecimal.valueOf(random.nextDouble());
        }

        if (type == Long.class || type == long.class) {
            return random.nextLong();
        }

        if (type == Short.class || type == short.class) {
            return (short) (random.nextInt(Short.MAX_VALUE + 1) * (random.nextBoolean() ? 1 : -1));
        }

        if (type == Byte.class || type == byte.class) {
            final byte[] randomByte = new byte[1];
            random.nextBytes(randomByte);
            return randomByte[0];
        }

        if (type == Character.class || type == char.class) {
            return CHARACTERS[random.nextInt(CHARACTERS.length)];
        }

        if (type == String.class) {
            StringBuilder randomString = new StringBuilder();
            /* prevent zero length string lengths */
            for (int count = 0; count < random.nextInt(MAX_RANDOM_STRING_LENGTH + 1) + 1; count++) {
                randomString.append(getRandom(Character.class));
            }
            return randomString.toString();
        }

        if (type == Date.class)
            return new Date(random.nextLong());

        if (type == Calendar.class) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(random.nextLong());
            return calendar;
        }

        if (type == LocalDate.class) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(random.nextLong());
            return calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        if (type == LocalDateTime.class) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(random.nextLong());
            return calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        return null;
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
            LOGGER.debug("Unable to access field: " + field, e);
        }
    }

    static {
        SKIP_FIELD_GENERATION = new ArrayList<>();
        SKIP_FIELD_GENERATION.add(String.class);
    }
}
