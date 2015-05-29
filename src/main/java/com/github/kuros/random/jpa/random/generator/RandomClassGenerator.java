package com.github.kuros.random.jpa.random.generator;

import java.util.Collection;

/**
 * Created by Kumar Rohit on 5/16/15.
 */
public interface RandomClassGenerator {

    Collection<Class<?>> getTypes();

    Object doGenerate(Class<?> aClass);

}
