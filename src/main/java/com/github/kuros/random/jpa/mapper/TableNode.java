package com.github.kuros.random.jpa.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Kumar Rohit on 5/2/15.
 */
public final class TableNode {

    private Set<Class<?>> parentClasses;
    private List<Relation<?, ?>> relations;

    public static TableNode newInstance() {
        return new TableNode();
    }

    private TableNode() {
        this.parentClasses = new HashSet<Class<?>>();
        this.relations = new ArrayList<Relation<?, ?>>();
    }

    public TableNode addRelation(final Relation relation) {
        this.relations.add(relation);
        return this;
    }

    public TableNode addParent(final Class<?> parentClass) {
        parentClasses.add(parentClass);
        return this;
    }

    public Set<Class<?>> getParentClasses() {
        return parentClasses;
    }

    public List<Relation<?, ?>> getRelations() {
        return relations;
    }

}
