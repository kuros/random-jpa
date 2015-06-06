package com.github.kuros.random.jpa.random;

import com.github.kuros.random.jpa.testUtil.entity.Employee;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

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
public class RandomizeImplTest {
    @Test @Ignore
    public void shouldRandmoizeEmployeeClass() throws InstantiationException, IllegalAccessException {
        final RandomGenerator randomFactory = RandomGenerator.newInstance(Generator.newInstance());
        final Randomize randomize = RandomizeImpl.newInstance(randomFactory);
        final Employee randomEmployee = randomize.createRandom(Employee.class);

        assertNotNull(randomEmployee);
//        assertNotNull(randomEmployee.getEmployeeId());
//        assertNotNull(randomEmployee.getPersonId());
//        assertNotNull(randomEmployee.getSalary());
    }
}
