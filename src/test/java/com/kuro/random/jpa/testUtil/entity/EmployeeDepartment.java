package com.kuro.random.jpa.testUtil.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Kumar Rohit on 4/26/15.
 */
@Entity
@Table(name = "employee_department")
public class EmployeeDepartment {

    private Long id;
    @Column(name = "employee_id")
    private Long employeeId;
    @Column(name = "shift_id")
    private Integer shiftId;

    @Column(name = "department_id")
    private Integer departmentId;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(final Long employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getShiftId() {
        return shiftId;
    }

    public void setShiftId(final Integer shiftId) {
        this.shiftId = shiftId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(final Integer departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final EmployeeDepartment that = (EmployeeDepartment) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
