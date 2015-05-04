package com.kuro.random.jpa.mapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/2/15.
 */
public class TableNode<T> {

    private Class<T> tableClass;
    private Set<FieldValue> fieldValues;

    public static <T> TableNode<T> newInstance(final Class<T> tableEntity) {
        return new TableNode<T>(tableEntity);
    }

    private TableNode(final Class<T> tableClass) {
        this.tableClass = tableClass;
        this.fieldValues = new HashSet<FieldValue>();
    }

    public Set<FieldValue> addAttributes(final FieldValue fieldValue) {
        fieldValues.add(fieldValue);
        return fieldValues;
    }

    public Class<T> getTableClass() {
        return tableClass;
    }

    public Set<FieldValue> getFieldValues() {
        return fieldValues;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TableNode<?> tableNode = (TableNode<?>) o;

        return tableClass.equals(tableNode.tableClass);

    }

    @Override
    public int hashCode() {
        return tableClass.hashCode();
    }
}
