package com.kuro.random.jpa.persistor.random;

import com.kuro.random.jpa.persistor.random.generator.Generator;
import com.kuro.random.jpa.persistor.random.generator.RandomGenerator;
import com.kuro.random.jpa.testUtil.entity.Employee;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public class RandomizeImplTest {

    @Test
    public void shouldRandmoizeEmployeeClass() throws InstantiationException, IllegalAccessException {
        final RandomGenerator randomFactory = RandomGenerator.newInstance(Generator.newInstance());
        final Randomize randomize = RandomizeImpl.newInstance(randomFactory);
        final Employee randomEmployee = randomize.createRandom(Employee.class);

        assertNotNull(randomEmployee);
        assertNotNull(randomEmployee.getEmployeeId());
        assertNotNull(randomEmployee.getPersonId());
        assertNotNull(randomEmployee.getSalary());
    }
}
