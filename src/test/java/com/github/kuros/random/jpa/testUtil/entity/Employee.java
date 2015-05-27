package com.github.kuros.random.jpa.testUtil.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Kumar Rohit on 4/26/15.
 *
 * Depends on Person
 */
@Entity
@Table
public class Employee {

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "person_id")
    private Long personId;

    @Column
    private Double salary;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(final Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(final Long personId) {
        this.personId = personId;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(final Double salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Employee employee = (Employee) o;

        return !(employeeId != null ? !employeeId.equals(employee.employeeId) : employee.employeeId != null);

    }

    @Override
    public int hashCode() {
        return employeeId != null ? employeeId.hashCode() : 0;
    }
}
