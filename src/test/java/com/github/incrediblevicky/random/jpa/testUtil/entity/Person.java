package com.github.incrediblevicky.random.jpa.testUtil.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Kumar Rohit on 4/26/15.
 */
@Entity
@Table(name = "person")
public class Person {

    @Column(name = "person_id")
    private Long personId;
    private String firstName;
    private String lastName;

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(final Long personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Person person = (Person) o;

        return !(personId != null ? !personId.equals(person.personId) : person.personId != null);

    }

    @Override
    public int hashCode() {
        return personId != null ? personId.hashCode() : 0;
    }
}
