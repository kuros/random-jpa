package com.kuro.random.jpa.persistor.random.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/16/15.
 */
public final class Generator {

    private List<RandomAttributeGenerator> randomAttributeGenerators;
    private List<RandomClassGenerator> randomClassGenerators;

    private Generator() {
        randomAttributeGenerators = new ArrayList<RandomAttributeGenerator>();
        randomClassGenerators = new ArrayList<RandomClassGenerator>();
    }

    public static Generator newInstance() {
        return new Generator();
    }

    public Generator addAttributeGenerator(final RandomAttributeGenerator randomAttributeGenerator) {
        randomAttributeGenerators.add(randomAttributeGenerator);
        return this;
    }

    public Generator addClassGenerator(final RandomClassGenerator randomClassGenerator) {
        randomClassGenerators.add(randomClassGenerator);
        return this;
    }

    public List<RandomAttributeGenerator> getRandomAttributeGenerators() {
        return randomAttributeGenerators;
    }

    public List<RandomClassGenerator> getRandomClassGenerators() {
        return randomClassGenerators;
    }
}
