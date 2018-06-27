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
public class FieldWrapper {

    private final Class<?> initializationClass;
    private final Field field;
    private final String fieldName;
    private final String overriddenFieldName;

    public FieldWrapper(final Field field) {
        this.initializationClass = field.getDeclaringClass();
        this.field = field;
        this.fieldName = field.getName();
        this.overriddenFieldName = null;
    }

    public FieldWrapper(final Class<?> type, final Field field, final String overriddenFieldName) {
        this.initializationClass = type;
        this.field = field;
        this.fieldName = field.getName();
        this.overriddenFieldName = overriddenFieldName;
    }

    public Class getInitializationClass() {
        return initializationClass;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final FieldWrapper that = (FieldWrapper) o;

        return initializationClass.equals(that.initializationClass) && field.equals(that.field);

    }

    @Override
    public int hashCode() {
        int result = initializationClass.hashCode();
        result = 31 * result + field.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldWrapper{" +
                "initializationClass=" + initializationClass +
                ", field=" + field +
                ", fieldName='" + fieldName + '\'' +
                ", overriddenFieldName='" + overriddenFieldName + '\'' +
                '}';
    }
}
