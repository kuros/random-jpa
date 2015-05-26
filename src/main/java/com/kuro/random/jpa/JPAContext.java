package com.kuro.random.jpa;

import com.kuro.random.jpa.definition.HierarchyGenerator;
import com.kuro.random.jpa.definition.HierarchyGeneratorImpl;
import com.kuro.random.jpa.definition.RelationCreator;
import com.kuro.random.jpa.link.Dependencies;
import com.kuro.random.jpa.mapper.HierarchyGraph;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.persistor.Persistor;
import com.kuro.random.jpa.persistor.PersistorImpl;
import com.kuro.random.jpa.persistor.model.ResultMap;
import com.kuro.random.jpa.persistor.random.RandomizeImpl;
import com.kuro.random.jpa.persistor.random.generator.Generator;
import com.kuro.random.jpa.persistor.random.generator.RandomGenerator;
import com.kuro.random.jpa.provider.ForeignKeyRelation;
import com.kuro.random.jpa.provider.MetaModelProvider;
import com.kuro.random.jpa.provider.RelationshipProvider;
import com.kuro.random.jpa.resolver.CreationOrderResolver;
import com.kuro.random.jpa.resolver.CreationPlanResolver;
import com.kuro.random.jpa.resolver.EntityResolver;
import com.kuro.random.jpa.types.CreationOrder;
import com.kuro.random.jpa.types.CreationPlan;
import com.kuro.random.jpa.types.Plan;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 4/22/15.
 */
public final class JPAContext {

    private final EntityManager entityManager;
    private final Map<String, EntityType<?>> metaModelRelations;
    private HierarchyGraph hierarchyGraph;
    private Dependencies dependencies;
    private RandomGenerator generator;

    public static JPAContext newInstance(final EntityManager entityManager) {
        return new JPAContext(entityManager, null);
    }

    public static JPAContext newInstance(final EntityManager entityManager, final Dependencies customDependencies) {
        return new JPAContext(entityManager, customDependencies);
    }

    private JPAContext(final EntityManager entityManager, final Dependencies dependencies) {
        this.entityManager = entityManager;
        this.dependencies = dependencies;

        final MetaModelProvider metaModelProvider = MetaModelProvider.newInstance(entityManager);
        this.metaModelRelations = metaModelProvider.getMetaModelRelations();
        this.generator = RandomGenerator.newInstance(Generator.newInstance());

        initialize();
    }

    private void initialize() {
        final RelationshipProvider relationshipProvider = RelationshipProvider.newInstance(entityManager);
        final List<ForeignKeyRelation> foreignKeyRelations = relationshipProvider.getForeignKeyRelations();

        final List<Relation> relations = RelationCreator.from(metaModelRelations)
                .with(foreignKeyRelations)
                .with(dependencies)
                .generate();

        final HierarchyGenerator hierarchyGenerator = getHierarchyGenerator();
        hierarchyGraph = hierarchyGenerator.generate(relations);
    }

    public void addRandomGenerator(final Generator randomGenerator) {
        this.generator = RandomGenerator.newInstance(randomGenerator);
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
