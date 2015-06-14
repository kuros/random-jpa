package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.testUtil.entity.Person;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

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
    @Test
    public void testGetObjectValues() throws Exception {
        final Person person = new Person();
        person.setPersonId(1L);
        person.setFirstName("myName");

        final String printValues = Util.printValues(person);
        Assert.assertEquals("[personId: 1, firstName: myName, lastName: null]", printValues);
        System.out.println(new Date());
    }
}
