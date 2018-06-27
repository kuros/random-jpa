package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.link.Link;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.util.AttributeHelper;

import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class DependencyResolver {

    public static Set<Relation> resolveDependency(final Dependencies dependencies) {

        if (dependencies != null) {
            final List<Link> links = dependencies.getLinks();
            return generateRelations(links);
        }

        return new HashSet<Relation>();
    }

    public static Set<Relation> ignoreLinks(final Dependencies dependencies) {

        if (dependencies != null) {
            final List<Link> links = dependencies.getIgnoreLinks();
            return generateRelations(links);
        }

        return new HashSet<Relation>();
    }

    public static Set<Relation> generateRelations(final List<Link> links) {
        final Set<Relation> relations = new HashSet<Relation>();

        for (Link link : links) {
            final Field from = getFieldValue(link.getFrom());
            final Field to = getFieldValue(link.getTo());
            relations.add(Relation.newInstance(new FieldWrapper(from), new FieldWrapper(to)));
        }

        return relations;
    }

    private static Field getFieldValue(final Attribute attribute) {
        return AttributeHelper.getField(attribute);
    }
}
