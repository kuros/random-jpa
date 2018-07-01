package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.exception.RandomJPAException;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NumberUtilTest {

    @Test
    public void shouldReturnNullIfInputValueIsNull() {
        assertNull(NumberUtil.castNumber(Integer.TYPE, null));
    }

    @Test
    public void shouldReturnObjectIfTypeMatches() {
        final Integer integer = NumberUtil.castNumber(Integer.class, 1);
        assertEquals(1, integer.intValue());
    }

    @Test
    public void shouldCastLongToInteger() {
        final Integer integer = NumberUtil.castNumber(Integer.class, 1L);
        assertEquals(1, integer.intValue());

        final Integer integer2 = NumberUtil.castNumber(Integer.TYPE, 1L);
        assertEquals(1, integer2.intValue());
    }

    @Test
    public void shouldCastIntegerToLong() {
        final Long aLong = NumberUtil.castNumber(Long.class, 1);
        assertEquals(1, aLong.intValue());

        final Long aLong2 = NumberUtil.castNumber(Long.TYPE, 1);
        assertEquals(1, aLong2.intValue());
    }

    @Test
    public void shouldCastIntegerToShort() {
        final Short aShort = NumberUtil.castNumber(Short.class, 1);
        assertEquals(Short.valueOf("1"), aShort);

        final Short aShort2 = NumberUtil.castNumber(Short.TYPE, 1);
        assertEquals(Short.valueOf("1"), aShort2);
    }

    @Test
    public void shouldCastIntegerToFloat() {
        final Float aFloat = NumberUtil.castNumber(Float.class, 1);
        assertEquals(Float.valueOf("1"), aFloat, 0.00001);

        final Float aFloat2 = NumberUtil.castNumber(Float.TYPE, 1);
        assertEquals(Float.valueOf("1"), aFloat2, 0.00001);
    }

    @Test
    public void shouldCastIntegerToDouble() {
        final Double aDouble = NumberUtil.castNumber(Double.class, 1);
        assertEquals(Double.valueOf("1"), aDouble, 0.00001);

        final Double aDouble2 = NumberUtil.castNumber(Double.TYPE, 1);
        assertEquals(Double.valueOf("1"), aDouble2, 0.00001);
    }

    @Test
    public void shouldCastIntegerToByte() {
        final Byte aByte = NumberUtil.castNumber(Byte.class, 1);
        assertEquals(Byte.valueOf("1"), aByte, 0.00001);

        final Byte aByte2 = NumberUtil.castNumber(Byte.TYPE, 1);
        assertEquals(Byte.valueOf("1"), aByte2, 0.00001);
    }

    @Test
    public void shouldCastIntegerToBigDecimal() {
        final BigDecimal bigDecimal = NumberUtil.castNumber(BigDecimal.class, 1);
        assertEquals(BigDecimal.valueOf(1), bigDecimal);
    }

    @Test
    public void shouldCastIntegerToAtomicInteger() {
        final AtomicInteger atomicInteger = NumberUtil.castNumber(AtomicInteger.class, 1);
        assertEquals(new AtomicInteger(1).get(), atomicInteger.get());
    }

    @Test
    public void shouldCastIntegerToAtomicLong() {
        final AtomicLong atomicLong = NumberUtil.castNumber(AtomicLong.class, 1);
        assertEquals(new AtomicLong(1).get(), atomicLong.get());
    }

    @Test
    public void shouldCastIntegerToBigInteger() {
        final BigInteger bigInteger = NumberUtil.castNumber(BigInteger.class, 1);
        assertEquals(BigInteger.valueOf(1), bigInteger);
    }

    @Test
    public void shouldParseStringToNumber() {
        final String input = "1";
        assertEquals(Integer.valueOf(input), NumberUtil.parseNumber(Integer.class, input));

        assertEquals(Long.valueOf(input), NumberUtil.parseNumber(Long.class, input));

    }

    @Test(expected = RandomJPAException.class)
    public void shouldThrowExceptionIfNumberCannotBeParsed() {
        final String input = "abc";
        NumberUtil.parseNumber(Integer.class, input);
    }

    @Test
    public void shouldReturnDefaultValues() {

        assertEquals(false, NumberUtil.getDefaultValue(Boolean.TYPE));
        assertNull(NumberUtil.getDefaultValue(Boolean.class));

        assertEquals((byte)0, NumberUtil.getDefaultValue(Byte.TYPE));
        assertNull(NumberUtil.getDefaultValue(Byte.class));

        assertEquals((char)0, NumberUtil.getDefaultValue(Character.TYPE));
        assertNull(NumberUtil.getDefaultValue(Character.class));

        assertEquals(0.0d, (Double)NumberUtil.getDefaultValue(Double.TYPE), 0.000001);
        assertNull(NumberUtil.getDefaultValue(Double.class));

        assertEquals(0.0f, (Float) NumberUtil.getDefaultValue(Float.TYPE), 0.000001);
        assertNull(NumberUtil.getDefaultValue(Float.class));

        assertEquals(0, NumberUtil.getDefaultValue(Integer.TYPE));
        assertNull(NumberUtil.getDefaultValue(Integer.class));

        assertEquals(0L, NumberUtil.getDefaultValue(Long.TYPE));
        assertNull(NumberUtil.getDefaultValue(Long.class));

        assertEquals((short)0, NumberUtil.getDefaultValue(Short.TYPE));
        assertNull(NumberUtil.getDefaultValue(Short.class));
    }
}
