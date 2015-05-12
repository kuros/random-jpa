package com.kuro.random.jpa.definition;

import com.kuro.random.jpa.link.Dependencies;
import com.kuro.random.jpa.mapper.FieldValue;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.provider.ForeignKeyRelation;
import com.kuro.random.jpa.util.EntityTypeHelper;

import javax.persistence.metamodel.EntityType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/10/15.
 */
public class RelationCreator {
    private Map<String, EntityType<?>> metaModelRelations;
    private List<ForeignKeyRelation> foreignKeyRelations;
    private Dependencies dependencies;

    private RelationCreator() {
        metaModelRelations = new HashMap<String, EntityType<?>>();
        this.foreignKeyRelations = new ArrayList<ForeignKeyRelation>();
    }

    public static RelationCreator from(final Map<String, EntityType<?>> metaModelRelations) {
        final RelationCreator relationCreator = new RelationCreator();
        relationCreator.metaModelRelations.putAll(metaModelRelations);
        return relationCreator;
    }

    public RelationCreator with(final List<ForeignKeyRelation> foreignKeyRelations) {
        this.foreignKeyRelations.addAll(foreignKeyRelations);
        return this;
    }

    public RelationCreator with(final Dependencies dependencies) {
        this.dependencies = dependencies;
    }

    public List<Relation> generate() {
        final ArrayList<Relation> relations = new ArrayList<Relation>();

        for (ForeignKeyRelation foreignKeyRelation : foreignKeyRelations) {
            FieldValue from = getTableNode(metaModelRelations, foreignKeyRelation);
            final FieldValue to = getReferencedTableNode(metaModelRelations, foreignKeyRelation);
            final Relation relation = Relation.newInstance(from, to);
            relations.add(relation);
        }

        relations.
        return relations;
    }

    private FieldValue getTableNode(final Map<String, EntityType<?>> metaModelRelations, final ForeignKeyRelation foreignKeyRelation) {
        return getTableNode(metaModelRelations, foreignKeyRelation.getTable(), foreignKeyRelation.getAttribute());
    }

    private FieldValue getTableNode(final Map<String, EntityType<?>> metaModelRelations, final String table, final String attribute) {
        final EntityType<?> entityType = metaModelRelations.get(table);
        final Field field = EntityTypeHelper.getField(entityType, attribute);
        return FieldValue.newIntance(field);
    }

    private FieldValue getReferencedTableNode(final Map<String, EntityType<?>> metaModelRelations, final ForeignKeyRelation foreignKeyRelation) {
        return getTableNode(metaModelRelations, foreignKeyRelation.getReferencedTable(), foreignKeyRelation.getReferencedAttribute());
    }
}
