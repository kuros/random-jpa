package com.github.kuros.random.jpa.testUtil;

import com.github.kuros.random.jpa.random.simple.SimpleRandomGenerator;
import com.github.kuros.random.jpa.random.simple.SimpleRandomGeneratorFactory;

public class RandomFixture {

    private static SimpleRandomGenerator randomGenerator;

    static {
        randomGenerator = SimpleRandomGeneratorFactory.newInstance().create();
    }

    public static <T> T create(final Class<T> type) {
        return randomGenerator.getRandom(type);
    }
}
