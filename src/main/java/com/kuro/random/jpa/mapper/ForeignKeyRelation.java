package com.kuro.random.jpa.mapper;

/**
 * Created by Kumar Rohit on 4/23/15.
 */
public class ForeignKeyRelation {

    private String parentTable;
    private String parentAttribute;
    private String referencedTable;
    private String referencedAttribute;

    public static ForeignKeyRelation newInstance(final String parentTable, final String parentAttribute, final String referencedTable, final String referencedAttribute) {
        return new ForeignKeyRelation(parentTable, parentAttribute, referencedTable, referencedAttribute);
    }

    private ForeignKeyRelation(final String parentTable, final String parentAttribute, final String referencedTable, final String referencedAttribute) {
        this.parentTable = parentTable;
        this.parentAttribute = parentAttribute;
        this.referencedTable = referencedTable;
        this.referencedAttribute = referencedAttribute;
    }

    public String getParentTable() {
        return parentTable;
    }

    public String getParentAttribute() {
        return parentAttribute;
    }

    public String getReferencedTable() {
        return referencedTable;
    }

    public String getReferencedAttribute() {
        return referencedAttribute;
    }
}
