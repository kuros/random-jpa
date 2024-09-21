package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.exception.RandomJPAException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class NumberUtil {

    private static boolean defaultBoolean;
    private static byte defaultByte;
    private static short defaultShort;
    private static int defaultInt;
    private static long defaultLong;
    private static float defaultFloat;
    private static double defaultDouble;
    private static char defaultChar;

    @SuppressWarnings("unchecked")
    public static <T> T castNumber(final Class<T> type, final Object value) {
        Object returnValue = value;
        if (value == null || type == null || type.equals(value.getClass())) {
            return (T) returnValue;
        }

        if (value instanceof Number number) {
            if (Integer.TYPE == type || Integer.class == type) {
                returnValue = number.intValue();
            } else if (Long.TYPE == type || Long.class == type) {
                returnValue = number.longValue();
            } else if (Short.TYPE == type || Short.class == type) {
                returnValue = number.shortValue();
            } else if (Float.TYPE == type || Float.class == type) {
                returnValue = number.floatValue();
            } else if (Double.TYPE == type || Double.class == type) {
                returnValue = number.doubleValue();
            } else if (Byte.TYPE == type || Byte.class == type) {
                returnValue = number.byteValue();
            } else if (BigDecimal.class == type) {
                returnValue = new BigDecimal(number.toString());
            } else if (BigInteger.class == type) {
                returnValue = new BigInteger(number.toString());
            } else if (AtomicInteger.class == type) {
                returnValue = new AtomicInteger(number.intValue());
            } else if (AtomicLong.class == type) {
                returnValue = new AtomicLong(number.longValue());
            }
        }

        return (T) returnValue;
    }

    public static Object parseNumber(final Class<?> type, final String value) {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance();
        try {
            final Number parse = numberFormat.parse(value);
            return castNumber(type, parse);
        } catch (final ParseException e) {
            throw new RandomJPAException(e);
        }
    }

    public static Object getDefaultValue(final Class<?> type) {
        Object obj = null;
        if (type.isPrimitive()) {
            if (Boolean.TYPE == type) {
                obj = defaultBoolean;
            } else if (Character.TYPE == type) {
                obj = defaultChar;
            } else if (Byte.TYPE == type) {
                obj = defaultByte;
            } else if (Short.TYPE == type) {
                obj = defaultShort;
            } else if (Integer.TYPE == type) {
                obj = defaultInt;
            } else if (Long.TYPE == type) {
                obj = defaultLong;
            } else if (Double.TYPE == type) {
                obj = defaultDouble;
            } else if (Float.TYPE == type) {
                obj = defaultFloat;
            }
        }
        return obj;
    }
}
