package com.kuro.random.jpa.persistor.random.generator;

import com.kuro.random.jpa.persistor.random.adapter.RandomClassGeneratorAdapter;
import com.kuro.random.jpa.util.AttributeHelper;
import com.openpojo.random.RandomFactory;

import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/16/15.
 */
public final class RandomGenerator {

    private final Generator generator;
    private Map<Field, RandomAttributeGenerator> attributeGeneratorMap;
    private RandomFactory randomFactory;

    private RandomGenerator(final Generator generator) {
        this.generator = generator;
        attributeGeneratorMap = new HashMap<Field, RandomAttributeGenerator>();
        randomFactory = new RandomFactory();

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

    public static RandomGenerator newInstance(final Generator generator) {
        return new RandomGenerator(generator);
    }

    private void addRandomClassGenerator(final RandomClassGenerator randomClassGenerator) {
        randomFactory.addRandomGenerator(RandomClassGeneratorAdapter.adapt(randomClassGenerator));
    }

    private void addAttributeGenerator(final RandomAttributeGenerator randomAttributeGenerator) {
        final List<? extends Attribute> attributes = randomAttributeGenerator.getAttributes();
        for (Attribute attribute : attributes) {
            try {
                attributeGeneratorMap.put(AttributeHelper.getField(attribute), randomAttributeGenerator);
            } catch (final NoSuchFieldException e) {
            }
        }
    }

    public Object generateRandom(final Field field) {
        final RandomAttributeGenerator randomAttributeGenerator = attributeGeneratorMap.get(field);
        if (randomAttributeGenerator != null) {
            return randomAttributeGenerator.doGenerate();
        }

        return randomFactory.getRandomValue(field.getType());
    }

    public <T> T generateRandom(final Class<T> type) {
        return randomFactory.getRandomValue(type);
    }
}
