package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.Person;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
public class UtilTest {

    @Mock
    private Cache cache;
    @Mock
    private AttributeProvider attributeProvider;
    @Mock
    private EntityTableMapping entityTableMapping;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cache.getAttributeProvider()).thenReturn(attributeProvider);
        Mockito.when(attributeProvider.get(Mockito.any(Class.class))).thenReturn(entityTableMapping);
    }

    @Test
    public void testGetObjectValues() throws Exception {
        final Person person = new Person();
        person.setPersonId(1L);
        person.setFirstName("myName");

        final String printValues = Util.printValues(person);
        assertEquals("[personId: 1, firstName: myName, lastName: null]", printValues);
        System.out.println(new Date());
    }

    @Test
    public void shouldReturnEmptyStringForNullObjectWhenPrintingNull() throws Exception {
        assertEquals("", Util.printEntityId(cache, null));
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldPrintEmptyStringIfExcpetionOccurs() throws Exception {
        Mockito.when(entityTableMapping.getAttributeIds()).thenThrow(RuntimeException.class);
        final Z z = RandomFixture.create(Z.class);
        assertEquals("", Util.printEntityId(cache, z));
    }

    @Test
    public void shouldPrintIdsForValidObjectAndAttribute() throws Exception {
        final List<String> attributeIds = new ArrayList<String>();
        attributeIds.add("id");
        attributeIds.add("xId");
        Mockito.when(entityTableMapping.getAttributeIds()).thenReturn(attributeIds);
        final Z z = RandomFixture.create(Z.class);

        final String expected = "[id: " + z.getId() + ", xId: " + z.getxId() + "]";
        assertEquals(expected, Util.printEntityId(cache, z));
    }

    @Test
    public void shouldPrintIdsIfValidObjectButInvalidAttributes() throws Exception {
        final List<String> attributeIds = new ArrayList<String>();
        attributeIds.add("id");
        attributeIds.add("fId");
        Mockito.when(entityTableMapping.getAttributeIds()).thenReturn(attributeIds);
        final Z z = RandomFixture.create(Z.class);

        final String expected = "[id: " + z.getId() + "]";
        assertEquals(expected, Util.printEntityId(cache, z));
    }

    @Test(expected = RandomJPAException.class)
    public void shouldThrowExceptionIfObjectIsNull() throws Exception {
        Util.assertNotNull("Test Method", null);
    }

    @Test
    public void shouldNotThrowExceptionIfObjectIsNotNull() throws Exception {
        Util.assertNotNull("Test Method", "");
    }

    @Test
    public void testFormatMessage() throws Exception {
        final String input = "{0}, {1}";
        assertEquals("1, 2", Util.formatMessage(input, 1, 2));
    }
}
