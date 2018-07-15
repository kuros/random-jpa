package com.github.kuros.random.jpa.random.generator.types;

import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private static final Class<?>[] TYPES = {Date.class, Calendar.class, Time.class, Timestamp.class, LocalDateTime.class, LocalDate.class, LocalTime.class };
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

        final LocalDateTime calendar = getCalendar();
        final Date date = Date.from(calendar.atZone(ZoneId.systemDefault()).toInstant());

        Object random;
        if (aClass == Calendar.class) {
            final Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            random = instance;
        } else if (aClass == Time.class) {
            random = new Time(date.getTime());
        } else if (aClass == Timestamp.class) {
            random = new Timestamp(date.getTime());
        } else if (aClass == LocalDate.class) {
            random = calendar.toLocalDate();
        } else if (aClass == LocalTime.class) {
            random = calendar.toLocalTime();
        } else if (aClass == LocalDateTime.class) {
            random = calendar;
        } else {
            random = date;
        }

        return random;
    }

    private LocalDateTime getCalendar() {

        LocalDateTime now = LocalDateTime.now();

        now = getYear(now);
        now = getMonth(now);
        now = getDate(now);
        now = getHour(now);
        now = getMinute(now);
        now = getSecond(now);

        return now;
    }

    private LocalDateTime getSecond(final LocalDateTime now) {
        final int sec = RANDOM.nextInt(MAX_SECONDS);
        return RANDOM.nextBoolean() ? now.plusSeconds(sec) : now.minusSeconds(sec);
    }

    private LocalDateTime getMinute(final LocalDateTime now) {
        final int min = RANDOM.nextInt(MAX_MINUTE);
        return RANDOM.nextBoolean() ? now.plusMinutes(min) : now.minusMinutes(min);
    }

    private LocalDateTime getHour(final LocalDateTime now) {
        final int hour = RANDOM.nextInt(MAX_HOUR);
        return RANDOM.nextBoolean() ? now.plusHours(hour) : now.minusHours(hour);
    }

    private LocalDateTime getDate(final LocalDateTime now) {
        final int day = RANDOM.nextInt(MAX_DATE);
        return RANDOM.nextBoolean() ? now.plusDays(day) : now.minusDays(day);
    }

    private LocalDateTime getYear(final LocalDateTime now) {
        final int year = RANDOM.nextInt(MAX_YEAR);
        return RANDOM.nextBoolean() ? now.plusYears(year) : now.minusYears(year);
    }

    private LocalDateTime getMonth(final LocalDateTime now) {
        final int months = RANDOM.nextInt(MAX_MONTH);
        return RANDOM.nextBoolean() ? now.plusMonths(months) : now.minusMonths(months);
    }

    private final static class Instance {
        private static final RandomClassGenerator INSTANCE = new DateGenerator();

        private Instance() {
        }
    }
}
