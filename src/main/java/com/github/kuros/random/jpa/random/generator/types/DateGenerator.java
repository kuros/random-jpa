package com.github.kuros.random.jpa.random.generator.types;

import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

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
public final class DateGenerator implements RandomClassGenerator {
    private static final Class<?>[] TYPES = {Date.class, Calendar.class, Time.class, Timestamp.class};
    private static final Random RANDOM = new Random();
    private static final int MAX_YEAR = 10;
    private static final int MAX_MONTH = 12;
    private static final int MAX_DATE = 31;
    private static final int MAX_HOUR = 24;
    private static final int MAX_MINUTE = 60;
    private static final int MAX_SECONDS = 60;

    private DateGenerator() {
    }

    public static RandomClassGenerator getInstance() {
        return Instance.INSTANCE;
    }

    public Collection<Class<?>> getTypes() {
        return Arrays.asList(TYPES);
    }

    public Object doGenerate(final Class<?> aClass) {

        final Calendar calendar = getCalendar();

        Object random;
        if (aClass == Calendar.class) {
            random = calendar;
        } else if (aClass == Time.class) {
            random = new Time(calendar.getTimeInMillis());
        } else if (aClass == Timestamp.class) {
            random = new Timestamp(calendar.getTimeInMillis());
        } else {
            random = calendar.getTime();
        }
        return random;
    }

    private Calendar getCalendar() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, getYear());
        calendar.add(Calendar.MONTH, getMonth());
        calendar.add(Calendar.DATE, getDate());
        calendar.add(Calendar.HOUR, getHour());
        calendar.add(Calendar.MINUTE, getMinute());
        calendar.add(Calendar.SECOND, getSecond());
        return calendar;
    }

    private int getSecond() {
        return (RANDOM.nextInt(MAX_SECONDS) * (RANDOM.nextBoolean() ? 1 : -1));
    }

    private int getMinute() {
        return (RANDOM.nextInt(MAX_MINUTE) * (RANDOM.nextBoolean() ? 1 : -1));
    }

    private int getHour() {
        return (RANDOM.nextInt(MAX_HOUR) * (RANDOM.nextBoolean() ? 1 : -1));
    }

    private int getDate() {
        return (RANDOM.nextInt(MAX_DATE) * (RANDOM.nextBoolean() ? 1 : -1));
    }

    private int getYear() {
        return (RANDOM.nextInt(MAX_YEAR) * (RANDOM.nextBoolean() ? 1 : -1));
    }

    private int getMonth() {
        return (RANDOM.nextInt(MAX_MONTH) * (RANDOM.nextBoolean() ? 1 : -1));
    }

    private final static class Instance {
        private static final RandomClassGenerator INSTANCE = new DateGenerator();

        private Instance() {
        }
    }
}
