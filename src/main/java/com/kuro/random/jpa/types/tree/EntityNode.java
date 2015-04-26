package com.kuro.random.jpa.types.tree;

import javax.persistence.metamodel.EntityType;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Kumar Rohit on 4/25/15.
 */
public class EntityNode {

    private final String tableName;
    private final EntityType<?> entityClass;
    private final Set<EntityNode> parent;
    private final Set<EntityNode> childs;

    private EntityNode(final String tableName, final EntityType<?> entityClass) {
        this.tableName = tableName;
        this.entityClass = entityClass;
        this.parent = new TreeSet<EntityNode>();
        this.childs = new TreeSet<EntityNode>();
    }

    public static EntityNode newInstance(final String tableName, final EntityType<?> entityClass) {
        return new EntityNode(tableName, entityClass);
    }

    public Set<EntityNode> getChilds() {
        return this.childs;
    }

    public void addChild(final EntityNode entity) {
        this.childs.add(entity);
    }

    public void addParent(final EntityNode entity) {
        this.parent.add(entity);
    }

    public void removeParent(final EntityNode entity) {
        this.parent.remove(entity);
    }

    public boolean hasChilds() {
        return this.childs.size() > 0;
    }

    public boolean hasParents() {
        return this.parent.size() > 0;
    }

    public EntityType<?> getEntityType() {
        return this.entityClass;
    }

    public Set<EntityNode> getParent() {
        return this.parent;
    }

    public String toString() {
        return "EntityNode [entityClass=" + this.entityClass.getJavaType().getSimpleName() + ", TableName=" + this.tableName + "]";
    }
}
