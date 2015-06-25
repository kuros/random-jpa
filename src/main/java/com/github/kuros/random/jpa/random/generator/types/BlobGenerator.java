package com.github.kuros.random.jpa.random.generator.types;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;

import javax.sql.rowset.serial.SerialBlob;
import java.nio.charset.Charset;
import java.sql.Blob;
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
public final class BlobGenerator implements RandomClassGenerator {
    private static final Class<?>[] TYPES = {Blob.class};
    private static final Random RANDOM = new Random();
    private static final int MAX_ITER_COUNT = 32;

    private BlobGenerator() {
    }

    public static RandomClassGenerator getInstance() {
        return Instance.INSTANCE;
    }

    public Collection<Class<?>> getTypes() {
        return Arrays.asList(TYPES);
    }

    public Object doGenerate(final Class<?> aClass) {

        final RandomClassGenerator stringGenerator = StringGenerator.getInstance();

        String random = "";

        for (int i = 0; i < RANDOM.nextInt(MAX_ITER_COUNT); i++) {
            random += stringGenerator.doGenerate(String.class);
        }


        Blob blob;
        try {
            blob = new SerialBlob(random.getBytes(Charset.defaultCharset()));
        } catch (final SQLException e) {
            throw new RandomJPAException("");
        }
        return blob;
    }

    private final static class Instance {
        private static final RandomClassGenerator INSTANCE = new BlobGenerator();

        private Instance() {
        }
    }
}
