package com.github.incrediblevicky.random.jpa.util;

/**
 * Created by Kumar Rohit on 5/19/15.
 */
public class NumberUtil {

    public static Object castNumber(final Class<?> type, final Object value) {
        Object returnValue = value;
        if (value == null || type.equals(value.getClass())) {
            return returnValue;
        }

        if (value instanceof Number) {
            final  Number number = (Number) value;
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
            }
        }

        return returnValue;
    }
}
