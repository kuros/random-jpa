package com.github.kuros.random.jpa.random.simple;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;
import com.github.kuros.random.jpa.random.generator.RandomFactory;
import com.github.kuros.random.jpa.random.generator.RandomFieldGenerator;

import java.util.HashMap;
import java.util.Map;
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
public final class SimpleRandomGeneratorFactory {

    private final RandomFactory randomFactory;
    private Map<Class<?>, RandomFieldGenerator> randomFieldGeneratorMap;

    private SimpleRandomGeneratorFactory() {
        this.randomFactory = new RandomFactory();
        this.randomFieldGeneratorMap = new HashMap<>();
    }

    public static SimpleRandomGeneratorFactory newInstance() {
        return new SimpleRandomGeneratorFactory();
    }

    public SimpleRandomGeneratorFactory with(final RandomClassGenerator... randomClassGenerators) {
        for (final RandomClassGenerator randomClassGenerator : randomClassGenerators) {
            randomFactory.addRandomGenerator(randomClassGenerator);
        }

        return this;
    }
    
    public SimpleRandomGeneratorFactory with(final RandomFieldGenerator... randomFieldGenerators) {
        for (RandomFieldGenerator randomFieldGenerator : randomFieldGenerators) {
            randomFieldGeneratorMap.put(randomFieldGenerator.getType(), randomFieldGenerator);
        }

        return this;
    }

    public SimpleRandomGenerator create() {
        validate();
        return new SimpleRandomGenerator(randomFactory, randomFieldGeneratorMap);
    }

    private void validate() {
        for (Class<?> aClass : randomFieldGeneratorMap.keySet()) {
            final RandomFieldGenerator randomFieldGenerator = randomFieldGeneratorMap.get(aClass);
            final Set<String> fieldNames = randomFieldGenerator.getFieldNames();
            for (String fieldName : fieldNames) {
                try {
                    aClass.getDeclaredField(fieldName);
                } catch (final NoSuchFieldException e) {
                    throw new RandomJPAException("No Such field found, Class: " + aClass.getName() + ", field: " + fieldName, e);
                }
            }
        }
    }
}
