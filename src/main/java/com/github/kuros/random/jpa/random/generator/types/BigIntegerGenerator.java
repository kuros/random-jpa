package com.github.kuros.random.jpa.random.generator.types;

import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
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
public final class BigIntegerGenerator implements RandomClassGenerator {

    private static final Class<?>[] TYPES = {BigInteger.class};
    private static final Random RANDOM = new Random();

    private BigIntegerGenerator() {
    }

    public static RandomClassGenerator getInstance() {
        return Instance.INSTANCE;
    }

    public Collection<Class<?>> getTypes() {
        return Arrays.asList(TYPES);
    }

    public Object doGenerate(final Class<?> aClass) {
        return BigInteger.valueOf(RANDOM.nextLong());
    }

    private final static class Instance {
        private static final RandomClassGenerator INSTANCE = new BigIntegerGenerator();

        private Instance() {
        }
    }
}
