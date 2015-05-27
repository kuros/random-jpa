package com.github.kuros.random.jpa.provider;

/**
 * Created by Kumar Rohit on 4/23/15.
 */
public final class ForeignKeyRelation {

    private String table;
    private String attribute;
    private String referencedTable;
    private String referencedAttribute;

    public static ForeignKeyRelation newInstance(final String parentTable, final String parentAttribute, final String referencedTable, final String referencedAttribute) {
        return new ForeignKeyRelation(parentTable, parentAttribute, referencedTable, referencedAttribute);
    }

    private ForeignKeyRelation(final String table, final String attribute, final String referencedTable, final String referencedAttribute) {
        this.table = table;
        this.attribute = attribute;
        this.referencedTable = referencedTable;
        this.referencedAttribute = referencedAttribute;
    }

    public String getTable() {
        return table;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public String getReferencedAttribute() {
        return referencedAttribute;
    }
}
