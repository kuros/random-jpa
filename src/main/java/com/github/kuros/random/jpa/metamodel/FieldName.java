package com.github.kuros.random.jpa.metamodel;

import java.lang.reflect.Field;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public class FieldName {

    private final Field field;
    private final String fieldName;
    private final String overridenFieldName;

    public FieldName(final Field field, final String overridenFieldName) {
        this.field = field;
        this.fieldName = field.getName();
        this.overridenFieldName = overridenFieldName;
    }

    public Field getField() {
        return field;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOverridenFieldName() {
        return overridenFieldName;
    }
}
