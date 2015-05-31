package com.github.kuros.random.jpa.metamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/31/15.
 */
public class EntityTableMapping {

    private Class<?> entityClass;
    private String entityName;
    private String tableName;

    private List<String> attributeIds;
    private List<String> columnIds;

    private Map<String, String> attributeColumnMapping;
    private Map<String, String> columnAttributeMapping;

    public EntityTableMapping(final Class<?> entityClass) {
        this.entityClass = entityClass;
        this.entityName = entityClass.getName();
        this.attributeIds = new ArrayList<String>();
        this.columnIds = new ArrayList<String>();
        this.attributeColumnMapping = new HashMap<String, String>();
        this.columnAttributeMapping = new HashMap<String, String>();
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    public void addAttributeIds(final String id) {
        attributeIds.add(id);
    }

    public void addColumnIds(final String[] ids) {
        for (String id : ids) {
            columnIds.add(id);
        }
    }

    public void addAttributeColumnMapping(final String attributeName, final String columnName) {
        attributeColumnMapping.put(attributeName, columnName);
        columnAttributeMapping.put(columnName, attributeName);
    }

    public String getColumnName(final String attributeName) {
        return attributeColumnMapping.get(attributeName);
    }

    public String getAttributeName(final String columnName) {
        return columnAttributeMapping.get(columnName);
    }

    public List<String> getColumnIds() {
        return columnIds;
    }

    public List<String> getAttributeIds() {
        return attributeIds;
    }

    public Set<String> getColumnNames() {
        return columnAttributeMapping.keySet();
    }

    public Set<String> getAttributeNames() {
        return attributeColumnMapping.keySet();
    }
}
