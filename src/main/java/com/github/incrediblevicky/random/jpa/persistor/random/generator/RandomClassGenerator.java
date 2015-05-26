package com.github.incrediblevicky.random.jpa.persistor.random.generator;

import java.util.Collection;

/**
 * Created by Kumar Rohit on 5/16/15.
 */
public interface RandomClassGenerator {

    Collection<Class<?>> getTypes();

    Object doGenerate(Class<?> aClass);

}
