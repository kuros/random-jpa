package com.github.kuros.random.jpa.provider.model;

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
public class ColumnDetail {
    private Integer stringLength;
    private Integer precision;
    private Integer scale;

    public ColumnDetail(final Integer stringLength) {
        this.stringLength = stringLength;
    }

    public ColumnDetail(final Integer stringLength, final Integer precision, final Integer scale) {
        this.stringLength = stringLength;
        this.precision = precision;
        this.scale = scale;
    }

    public Integer getStringLength() {
        return stringLength;
    }

    public Integer getPrecision() {
        return precision;
    }

    public Integer getScale() {
        return scale;
    }
}
