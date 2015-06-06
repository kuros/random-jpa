package com.github.kuros.random.jpa.metamodel.model;

import java.lang.reflect.Field;

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
public class FieldName {

    private final Field field;
    private final String fieldName;
    private final String overriddenFieldName;

    public FieldName(final Field field, final String overriddenFieldName) {
        this.field = field;
        this.fieldName = field.getName();
        this.overriddenFieldName = overriddenFieldName;
    }

    public Field getField() {
        return field;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOverriddenFieldName() {
        return overriddenFieldName;
    }
}
