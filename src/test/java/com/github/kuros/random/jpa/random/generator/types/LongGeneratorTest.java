package com.github.kuros.random.jpa.random.generator.types;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
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
public class LongGeneratorTest {

    @Test
    public void testGetTypes() {
        final Collection<Class<?>> types = LongGenerator.getInstance().getTypes();
        final List<Class<?>> classes = new ArrayList<>(types);
        assertEquals(3, classes.size());
        assertEquals(Long.TYPE, classes.get(0));
        assertEquals(Long.class, classes.get(1));
        assertEquals(Number.class, classes.get(2));
    }

    @Test
    public void testDoGenerate() {
        final Object actual = LongGenerator.getInstance().doGenerate(null);
        assertNotNull(actual);
        assertTrue(actual instanceof Long);
    }
}
