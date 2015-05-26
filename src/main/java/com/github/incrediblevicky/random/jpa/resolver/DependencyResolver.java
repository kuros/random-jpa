package com.github.incrediblevicky.random.jpa.resolver;

import com.github.incrediblevicky.random.jpa.mapper.Relation;
import com.github.incrediblevicky.random.jpa.link.Dependencies;
import com.github.incrediblevicky.random.jpa.link.Link;
import com.github.incrediblevicky.random.jpa.mapper.FieldValue;
import com.github.incrediblevicky.random.jpa.util.AttributeHelper;

import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/12/15.
 */
public class DependencyResolver {

    public static List<Relation> resolveDependency(final Dependencies dependencies) {
        final List<Relation> relations = new ArrayList<Relation>();

        if (dependencies != null) {
            final List<Link> links = dependencies.getLinks();
            for (Link link : links) {
                try {
                    final FieldValue from = getFieldValue(link.getFrom());
                    final FieldValue to = getFieldValue(link.getTo());
                    relations.add(Relation.newInstance(from, to));
                } catch (final NoSuchFieldException e) {
                }
            }
        }

        return relations;
    }

    private static FieldValue getFieldValue(final Attribute attribute) throws NoSuchFieldException {
        final Field field = AttributeHelper.getField(attribute);
        return FieldValue.newInstance(field);
    }
}
