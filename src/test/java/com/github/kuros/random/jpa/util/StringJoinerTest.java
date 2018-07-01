package com.github.kuros.random.jpa.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StringJoinerTest {

    @Test
    public void shouldReturnGivenStringWithNext() {
        final StringJoiner stringJoiner = new StringJoiner("a", "b", "c");

        assertEquals("a", stringJoiner.next());
        assertEquals("b", stringJoiner.next());
        assertEquals("c", stringJoiner.next());
        assertEquals("c", stringJoiner.next());
    }

    @Test
    public void shouldReturnCommaPrecedeedByBlank() {

        final StringJoiner comma = StringJoiner.comma();

        assertEquals("", comma.next());
        assertEquals(", ", comma.next());
    }

    @Test
    public void validateToString() {
        StringJoiner joiner = new StringJoiner("a", "b", "c");

        assertEquals("StringJoiner(items=3, index=0)", joiner.toString());
        joiner.next();
        assertEquals("StringJoiner(items=3, index=1)", joiner.toString());
        joiner.next();
        assertEquals("StringJoiner(items=3, index=2)", joiner.toString());
        joiner.next();
        assertEquals("StringJoiner(items=3, index=3)", joiner.toString());
    }
}