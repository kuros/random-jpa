package com.github.incrediblevicky.random.jpa.provider;

/**
 * Created by Kumar Rohit on 5/13/15.
 */
public final class ColumnCharacterLength {

    private final String tableName;
    private final String columnName;
    private final Integer length;

    private ColumnCharacterLength(final String tableName, final String columnName, final Integer length) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.length = length;
    }

    public static ColumnCharacterLength newInstance(final String tableName, final String columnName, final Integer length) {
        return new ColumnCharacterLength(tableName, columnName, length);
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Integer getLength() {
        return length;
    }

}
