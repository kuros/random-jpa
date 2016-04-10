package com.github.kuros.random.jpa.random.generator;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.util.AttributeHelper;

import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class RandomGenerator {

    private final Generator generator;
    private Map<Field, RandomAttributeGenerator> attributeGeneratorMap;
    private RandomFactory randomFactory;
    private SQLCharacterLengthProvider sqlCharacterLengthProvider;

    private RandomGenerator(final Cache cache, final Generator generator) {
        this.generator = generator;
        attributeGeneratorMap = new HashMap<Field, RandomAttributeGenerator>();
        randomFactory = new RandomFactory();
        sqlCharacterLengthProvider = cache.getSqlCharacterLengthProvider();
        init();
    }

    private void init() {
        final List<RandomAttributeGenerator> randomAttributeGenerators = generator.getRandomAttributeGenerators();
        for (RandomAttributeGenerator randomAttributeGenerator : randomAttributeGenerators) {
            addAttributeGenerator(randomAttributeGenerator);
        }

        final List<RandomClassGenerator> randomClassGenerators = generator.getRandomClassGenerators();
        for (RandomClassGenerator randomClassGenerator : randomClassGenerators) {
            addRandomClassGenerator(randomClassGenerator);
        }
    }

    public static RandomGenerator newInstance(final Cache cache, final Generator generator) {
        return new RandomGenerator(cache, generator);
    }

    public static RandomGenerator newInstance(final Cache cache) {
        return new RandomGenerator(cache, Generator.newInstance());
    }

    public Object generateRandom(final Field field) {
        final RandomAttributeGenerator randomAttributeGenerator = attributeGeneratorMap.get(field);
        if (randomAttributeGenerator != null) {
            return applyLengthConstraint(field, randomAttributeGenerator.doGenerate());
        }

        return applyLengthConstraint(field, randomFactory.generateRandom(field.getType()));
    }

    private Object applyLengthConstraint(final Field field, final Object o) {
        return sqlCharacterLengthProvider.applyLengthConstraint(field.getDeclaringClass().getName(), field.getName(), o);
    }

    public <T> T generateRandom(final Class<T> type) {
        return randomFactory.generateRandom(type);
    }

    public boolean isRandomAttributeGeneratorProvided(final Field field) {
        return attributeGeneratorMap.get(field) != null;
    }

    private void addRandomClassGenerator(final RandomClassGenerator randomClassGenerator) {
        randomFactory.addRandomGenerator(randomClassGenerator);
    }

    private void addAttributeGenerator(final RandomAttributeGenerator randomAttributeGenerator) {
        final List<? extends Attribute> attributes = randomAttributeGenerator.getAttributes();
        for (Attribute attribute : attributes) {
            try {
                attributeGeneratorMap.put(AttributeHelper.getField(attribute), randomAttributeGenerator);
            } catch (final NoSuchFieldException e) {
                //do nothing
            }
        }
    }
}
