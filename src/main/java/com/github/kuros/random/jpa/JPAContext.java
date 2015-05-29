package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.definition.HierarchyGenerator;
import com.github.kuros.random.jpa.mapper.HierarchyGraph;
import com.github.kuros.random.jpa.persistor.PersistorImpl;
import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.persistor.random.RandomizeImpl;
import com.github.kuros.random.jpa.resolver.CreationOrderResolver;
import com.github.kuros.random.jpa.resolver.CreationPlanResolver;
import com.github.kuros.random.jpa.resolver.EntityResolver;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.definition.HierarchyGeneratorImpl;
import com.github.kuros.random.jpa.persistor.Persistor;
import com.github.kuros.random.jpa.persistor.random.generator.Generator;
import com.github.kuros.random.jpa.persistor.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;

import javax.persistence.EntityManager;

/**
 * Created by Kumar Rohit on 4/22/15.
 */
public final class JPAContext {

    private final EntityManager entityManager;
    private HierarchyGraph hierarchyGraph;
    private RandomGenerator generator;

    static JPAContext newInstance(final EntityManager entityManager,
                                  final Generator generator, final HierarchyGraph hierarchyGraph) {
        return new JPAContext(entityManager, generator, hierarchyGraph);
    }

    private JPAContext(final EntityManager entityManager, final Generator generator, final HierarchyGraph hierarchyGraph) {
        this.entityManager = entityManager;
        this.hierarchyGraph = hierarchyGraph;
        this.generator = RandomGenerator.newInstance(generator);
    }

    public CreationPlan create(final Plan plan) {

        final EntityResolver entityResolver = EntityResolver.newInstance(entityManager, hierarchyGraph, plan);
        generator.addFieldValue(entityResolver.getFieldValueMap());

        final CreationOrderResolver creationOrderResolver = CreationOrderResolver.newInstance(hierarchyGraph, plan);
        final CreationOrder creationOrder = creationOrderResolver.getCreationOrder();

        final CreationPlanResolver creationPlanResolver = CreationPlanResolver.newInstance(creationOrder, RandomizeImpl.newInstance(generator));
        final CreationPlan creationPlan = creationPlanResolver.create();

        return creationPlan;
    }

    public ResultMap persist(final CreationPlan creationPlan) {
        final Persistor persistor = PersistorImpl.newInstance(entityManager);
        return persistor.persist(creationPlan);
    }

    private HierarchyGenerator getHierarchyGenerator() {
        return new HierarchyGeneratorImpl();
    }
}
