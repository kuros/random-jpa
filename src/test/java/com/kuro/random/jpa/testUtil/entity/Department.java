package com.kuro.random.jpa.testUtil.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Kumar Rohit on 4/26/15.
 */
@Entity
@Table(name = "department")
public class Department {

    private Integer departmentId;
    private String departmentName;

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(final Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(final String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Department that = (Department) o;

        return !(departmentId != null ? !departmentId.equals(that.departmentId) : that.departmentId != null);

    }

    @Override
    public int hashCode() {
        return departmentId != null ? departmentId.hashCode() : 0;
    }
}
