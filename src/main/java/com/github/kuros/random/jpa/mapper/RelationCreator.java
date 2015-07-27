package com.github.kuros.random.jpa.mapper;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.metamodel.MetaModelProvider;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import com.github.kuros.random.jpa.provider.model.ForeignKeyRelation;
import com.github.kuros.random.jpa.resolver.DependencyResolver;

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
    private final Map<String, List<FieldWrapper>> fieldsByTableName;
    private List<ForeignKeyRelation> foreignKeyRelations;
    private Dependencies dependencies;

    private RelationCreator(final MetaModelProvider metaModelProvider) {
        fieldsByTableName = metaModelProvider.getFieldsByTableName();
        this.foreignKeyRelations = new ArrayList<ForeignKeyRelation>();
        this.dependencies = Dependencies.newInstance();
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
            final FieldWrapper from = getFieldWrapper(foreignKeyRelation);
            final FieldWrapper to = getReferencedFieldValue(foreignKeyRelation);

            if (from != null && to != null) {
                final Relation relation = Relation.newInstance(from, to);
                relations.add(relation);
            }
        }

        relations.addAll(DependencyResolver.resolveDependency(dependencies));
        return relations;
    }

    private FieldWrapper getReferencedFieldValue(final ForeignKeyRelation foreignKeyRelation) {
        return getFieldWrapper(foreignKeyRelation.getReferencedTable(), foreignKeyRelation.getReferencedAttribute());
    }

    private FieldWrapper getFieldWrapper(final ForeignKeyRelation foreignKeyRelation) {
        return getFieldWrapper(foreignKeyRelation.getTable(), foreignKeyRelation.getAttribute());
    }

    private FieldWrapper getFieldWrapper(final String table, final String attribute) {
        final List<FieldWrapper> fieldWrappers = fieldsByTableName.get(table);
        if (fieldWrappers != null) {
            for (FieldWrapper fieldWrapper : fieldWrappers) {
                if (isFieldFound(attribute, fieldWrapper)) {
                    return fieldWrapper;
                }
            }
        }
        return null;
    }

    private boolean isFieldFound(final String attribute, final FieldWrapper fieldWrapper) {
        return (fieldWrapper.getOverriddenFieldName() != null && fieldWrapper.getOverriddenFieldName().equals(attribute))
                || (fieldWrapper.getOverriddenFieldName() == null && fieldWrapper.getFieldName().equals(attribute));
    }
}
