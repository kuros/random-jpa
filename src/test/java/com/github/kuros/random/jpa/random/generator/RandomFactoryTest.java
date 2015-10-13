package com.github.kuros.random.jpa.random.generator;

import com.github.kuros.random.jpa.testUtil.entity.Person;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

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
public class RandomFactoryTest {

    @Test
    public void testName() throws Exception {

        final RandomFactory randomFactory = new RandomFactory();

        assertNotNull(randomFactory.generateRandom(BigDecimal.class));
        assertNotNull(randomFactory.generateRandom(BigInteger.class));
        assertNotNull(randomFactory.generateRandom(Blob.class));
        assertNotNull(randomFactory.generateRandom(Boolean.class));
        assertNotNull(randomFactory.generateRandom(Boolean.TYPE));
        assertNotNull(randomFactory.generateRandom(Byte.class));
        assertNotNull(randomFactory.generateRandom(Byte.TYPE));
        assertNotNull(randomFactory.generateRandom(Character.class));
        assertNotNull(randomFactory.generateRandom(Character.TYPE));
        assertNotNull(randomFactory.generateRandom(Clob.class));
        assertNotNull(randomFactory.generateRandom(Date.class));
        assertNotNull(randomFactory.generateRandom(Calendar.class));
        assertNotNull(randomFactory.generateRandom(Time.class));
        assertNotNull(randomFactory.generateRandom(Timestamp.class));
        assertNotNull(randomFactory.generateRandom(Double.class));
        assertNotNull(randomFactory.generateRandom(Double.TYPE));
        assertNotNull(randomFactory.generateRandom(Float.class));
        assertNotNull(randomFactory.generateRandom(Float.TYPE));
        assertNotNull(randomFactory.generateRandom(Integer.class));
        assertNotNull(randomFactory.generateRandom(Integer.TYPE));
        assertNotNull(randomFactory.generateRandom(Long.class));
        assertNotNull(randomFactory.generateRandom(Long.TYPE));
        assertNotNull(randomFactory.generateRandom(Number.class));
        assertNotNull(randomFactory.generateRandom(Short.class));
        assertNotNull(randomFactory.generateRandom(Short.TYPE));
        assertNotNull(randomFactory.generateRandom(String.class));

        assertNotNull(randomFactory.generateRandom(Person.class));
        assertNotNull(randomFactory.generateRandom(PrivateConstructorClass.class));
        assertNotNull(randomFactory.generateRandom(TestEnum.class));
        assertNotNull(randomFactory.generateRandom(PrivateConstructorWithStaticMethodClass.class));
        assertNotNull(randomFactory.generateRandom(ClassAsParameter.class));


    }

    private class PrivateConstructorClass {
        private int i;
        private String s;

        private PrivateConstructorClass() {
        }

        PrivateConstructorClass(final int i) {
            this.i = i;
        }
    }

    private final static class PrivateConstructorWithStaticMethodClass {
        private int i;
        private String s;

        private PrivateConstructorWithStaticMethodClass(final int i) {
            this.i = i;
        }

        public static PrivateConstructorWithStaticMethodClass getInstance(final int i) {
            return new PrivateConstructorWithStaticMethodClass(i);
        }

        public PrivateConstructorWithStaticMethodClass setS(final String s1) {
            this.s = s1;
            return this;
        }
    }

    private final static class ClassAsParameter {
        private PrivateConstructorWithStaticMethodClass methodClass;

        ClassAsParameter(final PrivateConstructorWithStaticMethodClass methodClass) {
            this.methodClass = methodClass;
        }
    }

    private enum TestEnum {
        VAL1,
        VAL2,
        VAL3
    }
}
