package com.github.kuros.random.jpa.mapper;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.metamodel.FieldName;
import com.github.kuros.random.jpa.metamodel.MetaModelProvider;
import com.github.kuros.random.jpa.provider.ForeignKeyRelation;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import com.github.kuros.random.jpa.resolver.DependencyResolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            final Field from = getFieldValue(foreignKeyRelation);
            final Field to = getReferencedFieldValue(foreignKeyRelation);

            if (from != null && to != null) {
                final Relation relation = Relation.newInstance(from, to);
                relations.add(relation);
            }
        }

        relations.addAll(DependencyResolver.resolveDependency(dependencies));
        return relations;
    }

    private Field getReferencedFieldValue(final ForeignKeyRelation foreignKeyRelation) {
        return getFieldValue(foreignKeyRelation.getReferencedTable(), foreignKeyRelation.getReferencedAttribute());
    }

    private Field getFieldValue(final ForeignKeyRelation foreignKeyRelation) {
        return getFieldValue(foreignKeyRelation.getTable(), foreignKeyRelation.getAttribute());
    }

    private Field getFieldValue(final String table, final String attribute) {
        final List<FieldName> fieldNames = fieldsByTableName.get(table);
        if (fieldNames != null) {
            for (FieldName fieldName : fieldNames) {
                if (isFieldFound(attribute, fieldName)) {
                    return fieldName.getField();
                }
            }
        }
        return null;
    }

    private boolean isFieldFound(final String attribute, final FieldName fieldName) {
        return (fieldName.getOverriddenFieldName() != null && fieldName.getOverriddenFieldName().equals(attribute))
                || (fieldName.getOverriddenFieldName() == null && fieldName.getFieldName().equals(attribute));
    }
}
