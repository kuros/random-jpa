package com.github.kuros.random.jpa.random.generator.types;

import org.junit.Test;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
public class DateGeneratorTest {

    @Test
    public void testGetTypes() {
        final Collection<Class<?>> types = DateGenerator.getInstance().getTypes();
        final List<Class<?>> classes = new ArrayList<>(types);
        assertEquals(8, classes.size());
        assertEquals(Date.class, classes.get(0));
        assertEquals(Calendar.class, classes.get(1));
        assertEquals(Time.class, classes.get(2));
        assertEquals(Timestamp.class, classes.get(3));
        assertEquals(LocalDateTime.class, classes.get(4));
        assertEquals(LocalDate.class, classes.get(5));
        assertEquals(LocalTime.class, classes.get(6));
        assertEquals(Instant.class, classes.get(7));
    }

    @Test
    public void shouldReturnRandomDateForNullParam() {
        final Object actual = DateGenerator.getInstance().doGenerate(null);
        assertNotNull(actual);
        assertTrue(actual instanceof Date);
    }

    @Test
    public void testDoGenerate() {
        final Object actual = DateGenerator.getInstance().doGenerate(Date.class);
        assertNotNull(actual);
        assertTrue(actual instanceof Date);

        final Object actual2 = DateGenerator.getInstance().doGenerate(Calendar.class);
        assertNotNull(actual2);
        assertTrue(actual2 instanceof Calendar);

        final Object actual3 = DateGenerator.getInstance().doGenerate(Time.class);
        assertNotNull(actual3);
        assertTrue(actual3 instanceof Time);

        final Object actual4 = DateGenerator.getInstance().doGenerate(Timestamp.class);
        assertNotNull(actual4);
        assertTrue(actual4 instanceof Timestamp);

        final Object actual5 = DateGenerator.getInstance().doGenerate(LocalDate.class);
        assertNotNull(actual5);
        assertTrue(actual5 instanceof LocalDate);

        final Object actual6 = DateGenerator.getInstance().doGenerate(LocalTime.class);
        assertNotNull(actual6);
        assertTrue(actual6 instanceof LocalTime);

        final Object actual7 = DateGenerator.getInstance().doGenerate(LocalDateTime.class);
        assertNotNull(actual7);
        assertTrue(actual7 instanceof LocalDateTime);

        final Object actual8 = DateGenerator.getInstance().doGenerate(Instant.class);
        assertNotNull(actual8);
        assertTrue(actual8 instanceof Instant);
    }
}
