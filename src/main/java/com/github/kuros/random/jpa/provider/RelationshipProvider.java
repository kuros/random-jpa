package com.github.kuros.random.jpa.provider;

import java.util.List;

/**
 * Created by Kumar Rohit on 5/29/15.
 */
public interface RelationshipProvider {
    List<ForeignKeyRelation> getForeignKeyRelations();
}
