package com.github.kuros.random.jpa.testUtil.entity;

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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "E")
public class E {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "b_id")
    private long bId;

    @Column(name = "d_id")
    private long dId;

    @Column(name = "f_id")
    private long fId;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public long getbId() {
        return bId;
    }

    public void setbId(final long bId) {
        this.bId = bId;
    }

    public long getdId() {
        return dId;
    }

    public void setdId(final long dId) {
        this.dId = dId;
    }

    public long getfId() {
        return fId;
    }

    public void setfId(final long fId) {
        this.fId = fId;
    }
}
