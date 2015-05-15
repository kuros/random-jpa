package com.kuro.random.jpa.persistor.random;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public interface Randomize {

    <T> T createRandom(Class<T> type);
}
