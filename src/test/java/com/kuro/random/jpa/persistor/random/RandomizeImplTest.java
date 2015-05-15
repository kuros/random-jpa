package com.kuro.random.jpa.persistor.random;

import com.kuro.random.jpa.testUtil.entity.Employee;
import com.openpojo.random.RandomFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Kumar Rohit on 5/14/15.
 */
public class RandomizeImplTest {

    @Test
    public void shouldRandmoizeEmployeeClass() throws InstantiationException, IllegalAccessException {
        final RandomFactory randomFactory = new RandomFactory();
        final Randomize randomize = RandomizeImpl.newInstance(randomFactory);
        final Employee randomEmployee = randomize.createRandom(Employee.class);

        System.out.println(randomEmployee);
        System.out.println(randomEmployee.getEmployeeId());
        System.out.println(randomEmployee.getPersonId());
        System.out.println(randomEmployee.getSalary());


        assertNotNull(randomEmployee);
        assertNotNull(randomEmployee.getEmployeeId());
        assertNotNull(randomEmployee.getPersonId());
        assertNotNull(randomEmployee.getSalary());
    }
}