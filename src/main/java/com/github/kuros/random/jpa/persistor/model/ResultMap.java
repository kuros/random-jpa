package com.github.kuros.random.jpa.persistor.model;

import com.github.kuros.random.jpa.types.Printer;

import java.util.List;

/**
 * Created by Kumar Rohit on 5/29/15.
 *
 */
public interface ResultMap {
    @SuppressWarnings("unchecked")
    <T> T get(Class<T> type);

    @SuppressWarnings("unchecked")
    <T> T get(Class<T> type, int index);

    @SuppressWarnings("unchecked")
    <T> List<T> getAll(Class<T> type);

    void print(Printer printer);
}
