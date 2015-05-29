package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.mapper.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.ProcessingType;
import com.github.kuros.random.jpa.resolver.annotation.AnnotatedCreationOrderResolver;
import com.github.kuros.random.jpa.types.Plan;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public class CreationOrderResolverFactory {

    public static CreationOrderResolver getCreationOrderResolver(final ProcessingType processingType,
                                                                 final HierarchyGraph hierarchyGraph,
                                                                 final Plan plan) {
        switch (processingType) {
            case ANNOTATION:
                return AnnotatedCreationOrderResolver.newInstance(hierarchyGraph, plan);
            default:
                throw new UnsupportedOperationException("processing type should match to Annotation/XML");
        }
    }
}
