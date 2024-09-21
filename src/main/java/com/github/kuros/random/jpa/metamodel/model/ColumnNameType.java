package com.github.kuros.random.jpa.metamodel.model;

/**
 * Created by Kumar Rohit on 09/01/18.
 */
public final class ColumnNameType {

    private final String columnName;
    private final Type type;

    public ColumnNameType(final String columnName, final Type type) {
        this.columnName = columnName;
        this.type = type;
    }

    public String getColumnName() {
        return columnName;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        BASIC,
        MAPPED
    }
}
