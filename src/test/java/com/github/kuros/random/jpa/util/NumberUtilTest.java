package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NumberUtilTest {

    @Test
    public void shouldReturnNullIfInputValueIsNull() throws Exception {
        assertNull(NumberUtil.castNumber(Integer.TYPE, null));
    }

    @Test
    public void shouldReturnObjectIfTypeMatches() throws Exception {
        final Integer integer = NumberUtil.castNumber(Integer.class, 1);
        assertEquals(1, integer.intValue());
    }

    @Test
    public void shouldCastLongToInteger() throws Exception {
        final Integer integer = NumberUtil.castNumber(Integer.class, 1L);
        assertEquals(1, integer.intValue());

        final Integer integer2 = NumberUtil.castNumber(Integer.TYPE, 1L);
        assertEquals(1, integer2.intValue());
    }

    @Test
    public void shouldCastIntegerToLong() throws Exception {
        final Long aLong = NumberUtil.castNumber(Long.class, 1);
        assertEquals(1, aLong.intValue());

        final Long aLong2 = NumberUtil.castNumber(Long.TYPE, 1);
        assertEquals(1, aLong2.intValue());
    }

    @Test
    public void shouldCastIntegerToShort() throws Exception {
        final Short aShort = NumberUtil.castNumber(Short.class, 1);
        assertEquals(Short.valueOf("1"), aShort);

        final Short aShort2 = NumberUtil.castNumber(Short.TYPE, 1);
        assertEquals(Short.valueOf("1"), aShort2);
    }

    @Test
    public void shouldCastIntegerToFloat() throws Exception {
        final Float aFloat = NumberUtil.castNumber(Float.class, 1);
        assertEquals(Float.valueOf("1"), aFloat, 0.00001);

        final Float aFloat2 = NumberUtil.castNumber(Float.TYPE, 1);
        assertEquals(Float.valueOf("1"), aFloat2, 0.00001);
    }

    @Test
    public void shouldCastIntegerToDouble() throws Exception {
        final Double aDouble = NumberUtil.castNumber(Double.class, 1);
        assertEquals(Double.valueOf("1"), aDouble, 0.00001);

        final Double aDouble2 = NumberUtil.castNumber(Double.TYPE, 1);
        assertEquals(Double.valueOf("1"), aDouble2, 0.00001);
    }

    @Test
    public void shouldCastIntegerToByte() throws Exception {
        final Byte aByte = NumberUtil.castNumber(Byte.class, 1);
        assertEquals(Byte.valueOf("1"), aByte, 0.00001);

        final Byte aByte2 = NumberUtil.castNumber(Byte.TYPE, 1);
        assertEquals(Byte.valueOf("1"), aByte2, 0.00001);
    }

    @Test
    public void shouldParseStringToNumber() throws Exception {
        final String input = "1";
        assertEquals(Integer.valueOf(input), NumberUtil.parseNumber(Integer.class, input));

        assertEquals(Long.valueOf(input), NumberUtil.parseNumber(Long.class, input));

    }

    @Test(expected = RandomJPAException.class)
    public void shouldThrowExceptionIfNumberCannotBeParsed() throws Exception {
        final String input = "abc";
        NumberUtil.parseNumber(Integer.class, input);
    }
}
