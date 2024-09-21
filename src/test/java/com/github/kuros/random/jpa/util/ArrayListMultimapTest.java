package com.github.kuros.random.jpa.util;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArrayListMultimapTest {

    @Test
    public void shouldCreateACollectionWhenMultipleValuesAreAddedWithSameKey() {
        final Multimap<Integer, Integer> multimap = ArrayListMultimap.newArrayListMultimap();

        multimap.put(1, 4);
        multimap.put(1, 5);
        multimap.put(2, 6);

        assertEquals(2, multimap.getKeySet().size());
        assertTrue(multimap.getKeySet().contains(1));
        assertTrue(multimap.getKeySet().contains(2));

        final Collection<Integer> value1 = multimap.get(1);
        assertEquals(2, value1.size());
        assertTrue(value1.contains(4));
        assertTrue(value1.contains(5));

        final Collection<Integer> value2 = multimap.get(2);
        assertEquals(1, value2.size());
        assertTrue(value2.contains(6));


    }
}
