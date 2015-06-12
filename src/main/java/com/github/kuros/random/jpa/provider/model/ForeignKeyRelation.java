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
