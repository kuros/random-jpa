package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.definition.HierarchyGenerator;
import com.github.kuros.random.jpa.definition.HierarchyGeneratorImpl;
import com.github.kuros.random.jpa.mapper.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.ProcessingType;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.persistor.Persistor;
import com.github.kuros.random.jpa.persistor.PersistorFactory;
import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.random.RandomizeImpl;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.resolver.CreationOrderResolver;
import com.github.kuros.random.jpa.resolver.CreationPlanResolver;
import com.github.kuros.random.jpa.resolver.EntityResolver;
import com.github.kuros.random.jpa.resolver.EntityResolverFactory;
import com.github.kuros.random.jpa.resolver.annotation.AnnotatedCreationOrderResolver;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Plan;

import javax.persistence.EntityManager;

/**
 * Created by Kumar Rohit on 4/22/15.
 */
public final class JPAContext {

    private ProcessingType processingType;
    private final EntityManager entityManager;
    private HierarchyGraph hierarchyGraph;
    private RandomGenerator generator;

    static JPAContext newInstance(final ProcessingType processingType, final EntityManager entityManager,
                                  final Generator generator, final HierarchyGraph hierarchyGraph) {
        return new JPAContext(processingType, entityManager, generator, hierarchyGraph);
    }

    private JPAContext(final ProcessingType processingType, final EntityManager entityManager, final Generator generator, final HierarchyGraph hierarchyGraph) {
        this.processingType = processingType;
        this.entityManager = entityManager;
        this.hierarchyGraph = hierarchyGraph;
        this.generator = RandomGenerator.newInstance(generator);
    }

    public CreationPlan create(final Plan plan) {

        final EntityResolver entityResolver = EntityResolverFactory
                .getEntityResolver(processingType, entityManager, hierarchyGraph, plan);
        generator.addFieldValue(entityResolver.getFieldValueMap());

        final CreationOrderResolver creationOrderResolver = AnnotatedCreationOrderResolver.newInstance(AttributeProvider.getInstance(entityManager), hierarchyGraph, plan);
        final CreationOrder creationOrder = creationOrderResolver.getCreationOrder();

        final CreationPlanResolver creationPlanResolver = CreationPlanResolver.newInstance(creationOrder, RandomizeImpl.newInstance(generator));

        return creationPlanResolver.create();
    }

    public ResultMap persist(final CreationPlan creationPlan) {
        final Persistor persistor = PersistorFactory.getPersistor(processingType, entityManager);
        return persistor.persist(creationPlan);
    }

    public ResultMap createAndPersist(final Plan plan) {
        return persist(create(plan));
    }

    private HierarchyGenerator getHierarchyGenerator() {
        return new HierarchyGeneratorImpl();
    }
}
