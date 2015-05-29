package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.mapper.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.ProcessingType;
import com.github.kuros.random.jpa.resolver.annotation.AnnotatedEntityResolver;
import com.github.kuros.random.jpa.types.Plan;

import javax.persistence.EntityManager;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public class EntityResolverFactory {

    public static EntityResolver getEntityResolver(final ProcessingType processingType, final EntityManager entityManager, final HierarchyGraph hierarchyGraph, final Plan plan) {
        switch (processingType) {
            case ANNOTATION:
                return AnnotatedEntityResolver.newInstance(entityManager, hierarchyGraph, plan);

            default:
                throw new UnsupportedOperationException("processing type should match to Annotation/XML");
        }
    }
}
