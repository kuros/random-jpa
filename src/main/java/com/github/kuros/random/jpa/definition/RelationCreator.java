package com.github.kuros.random.jpa.definition;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.mapper.FieldValue;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.FieldName;
import com.github.kuros.random.jpa.metamodel.MetaModelProvider;
import com.github.kuros.random.jpa.provider.ForeignKeyRelation;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import com.github.kuros.random.jpa.resolver.DependencyResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/10/15.
 * Generates the list of relations.
 */
public final class RelationCreator {
    private final Map<String, List<FieldName>> fieldsByTableName;
    private List<ForeignKeyRelation> foreignKeyRelations;
    private Dependencies dependencies;

    private RelationCreator(final MetaModelProvider metaModelProvider) {
        fieldsByTableName = metaModelProvider.getFieldsByTableName();
        this.foreignKeyRelations = new ArrayList<ForeignKeyRelation>();
    }

    public static RelationCreator from(final MetaModelProvider metaModelProvider) {
        return new RelationCreator(metaModelProvider);
    }

    public RelationCreator with(final RelationshipProvider relationshipProvider) {
        this.foreignKeyRelations.addAll(relationshipProvider.getForeignKeyRelations());
        return this;
    }

    public RelationCreator with(final Dependencies dependency) {
        this.dependencies = dependency;
        return this;
    }

    public List<Relation> generate() {
        final ArrayList<Relation> relations = new ArrayList<Relation>();

        for (ForeignKeyRelation foreignKeyRelation : foreignKeyRelations) {
            final FieldValue from = getFieldValue(foreignKeyRelation);
            final FieldValue to = getReferencedFieldValue(foreignKeyRelation);

            if (from != null && to != null) {
                final Relation relation = Relation.newInstance(from, to);
                relations.add(relation);
            }
        }

        relations.addAll(DependencyResolver.resolveDependency(dependencies));
        return relations;
    }

    private FieldValue getReferencedFieldValue(final ForeignKeyRelation foreignKeyRelation) {
        return getFieldValue(foreignKeyRelation.getReferencedTable(), foreignKeyRelation.getReferencedAttribute());
    }

    private FieldValue getFieldValue(final ForeignKeyRelation foreignKeyRelation) {
        return getFieldValue(foreignKeyRelation.getTable(), foreignKeyRelation.getAttribute());
    }

    private FieldValue getFieldValue(final String table, final String attribute) {
        final List<FieldName> fieldNames = fieldsByTableName.get(table);
        if (fieldNames != null) {
            for (FieldName fieldName : fieldNames) {
                if (isFieldFound(attribute, fieldName)) {
                    return FieldValue.newInstance(fieldName.getField());
                }
            }
        }
        return null;
    }

    private boolean isFieldFound(final String attribute, final FieldName fieldName) {
        return (fieldName.getOverridenFieldName() != null && fieldName.getOverridenFieldName().equals(attribute))
                || (fieldName.getOverridenFieldName() == null && fieldName.getFieldName().equals(attribute));
    }
}
