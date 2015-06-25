package com.github.kuros.random.jpa.random.generator.types;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;

import javax.sql.rowset.serial.SerialClob;
import java.sql.Clob;
import java.sql.SQLException;
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
public final class ClobGenerator implements RandomClassGenerator {
    private static final Class<?>[] TYPES = {Clob.class};
    private static final Random RANDOM = new Random();
    private static final int MAX_ITER_COUNT = 32;

    private ClobGenerator() {
    }

    public static RandomClassGenerator getInstance() {
        return Instance.INSTANCE;
    }

    public Collection<Class<?>> getTypes() {
        return Arrays.asList(TYPES);
    }

    public Object doGenerate(final Class<?> aClass) {

        final RandomClassGenerator generator = CharacterGenerator.getInstance();

        final int count = RANDOM.nextInt(MAX_ITER_COUNT);

        final char[] chars = new char[count];

        for (int i = 0; i < count; i++) {
            chars[i] = (Character) generator.doGenerate(Character.class);
        }


        Clob clob;
        try {
            clob = new SerialClob(chars);
        } catch (final SQLException e) {
            throw new RandomJPAException("");
        }
        return clob;
    }

    private final static class Instance {
        private static final RandomClassGenerator INSTANCE = new ClobGenerator();

        private Instance() {
        }
    }
}
