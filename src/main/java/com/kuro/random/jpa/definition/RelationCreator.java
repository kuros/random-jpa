package com.kuro.random.jpa.definition;

import com.kuro.random.jpa.link.Dependencies;
import com.kuro.random.jpa.mapper.FieldValue;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.provider.ForeignKeyRelation;
import com.kuro.random.jpa.resolver.DependencyResolver;
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
public final class RelationCreator {
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

    public RelationCreator with(final List<ForeignKeyRelation> foreignKeyRelation) {
        this.foreignKeyRelations.addAll(foreignKeyRelation);
        return this;
    }

    public RelationCreator with(final Dependencies dependency) {
        this.dependencies = dependency;
        return this;
    }

    public List<Relation> generate() {
        final ArrayList<Relation> relations = new ArrayList<Relation>();

        for (ForeignKeyRelation foreignKeyRelation : foreignKeyRelations) {
            final FieldValue from = getTableNode(foreignKeyRelation);
            final FieldValue to = getReferencedTableNode(foreignKeyRelation);

            if (from != null && to != null) {
                final Relation relation = Relation.newInstance(from, to);
                relations.add(relation);
            }
        }

        relations.addAll(DependencyResolver.resolveDependency(dependencies));
        return relations;
    }

    private FieldValue getReferencedTableNode(final ForeignKeyRelation foreignKeyRelation) {
        return getTableNode(foreignKeyRelation.getReferencedTable(), foreignKeyRelation.getReferencedAttribute());
    }

    private FieldValue getTableNode(final ForeignKeyRelation foreignKeyRelation) {
        return getTableNode(foreignKeyRelation.getTable(), foreignKeyRelation.getAttribute());
    }

    private FieldValue getTableNode(final String table, final String attribute) {
        final EntityType<?> entityType = metaModelRelations.get(table);

        if (entityType == null) {
            return null;
        }

        final Field field = EntityTypeHelper.getField(entityType, attribute);
        return field != null ? FieldValue.newInstance(field) : null;
    }
}
