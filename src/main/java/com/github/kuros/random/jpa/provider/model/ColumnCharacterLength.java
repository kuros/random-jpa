package com.github.kuros.random.jpa.provider.model;

import java.util.HashMap;
import java.util.Map;

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
public final class ColumnCharacterLength {

    private final Map<String, ColumnDetail> nameColumnDetails;

    private ColumnCharacterLength() {
        this.nameColumnDetails = new HashMap<>();
    }

    public static ColumnCharacterLength newInstance() {
        return new ColumnCharacterLength();
    }

    public void add(final String columnName, final ColumnDetail columnDetail) {
        nameColumnDetails.put(columnName, columnDetail);
    }

    public Integer getLength(final String columnName) {
        return nameColumnDetails.get(columnName).getStringLength();
    }

    public ColumnDetail getColumnDetail(final String columnName) {
        return nameColumnDetails.get(columnName);
    }
}
