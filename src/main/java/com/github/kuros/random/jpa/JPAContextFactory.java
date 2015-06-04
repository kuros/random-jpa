package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.cache.PreconditionCache;
import com.github.kuros.random.jpa.definition.HierarchyGenerator;
import com.github.kuros.random.jpa.definition.HierarchyGeneratorImpl;
import com.github.kuros.random.jpa.definition.RelationCreator;
import com.github.kuros.random.jpa.link.Before;
import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.link.Preconditions;
import com.github.kuros.random.jpa.mapper.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.MetaModelProvider;
import com.github.kuros.random.jpa.metamodel.MetaModelProviderImpl;
import com.github.kuros.random.jpa.provider.RelationProviderFactory;
import com.github.kuros.random.jpa.random.generator.Generator;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public final class JPAContextFactory {

    private Database database;
    private EntityManager entityManager;
    private Dependencies dependencies;
    private Generator generator;
    private Preconditions preconditions;

    public static JPAContextFactory newInstance(final Database database, final EntityManager entityManager) {
        return new JPAContextFactory(database, entityManager);
    }

    private JPAContextFactory(final Database database, final EntityManager entityManager) {
        this.database = database;
        this.entityManager = entityManager;
        this.preconditions = new Preconditions();
    }

    public JPAContextFactory with(final Dependencies customDependencies) {
        this.dependencies = customDependencies;
        return this;
    }

    public JPAContextFactory with(final Generator randomGenerator) {
        this.generator = randomGenerator;
        return this;
    }


    public JPAContextFactory withPreconditions(final Before... befores) {
        for (Before before : befores) {
            preconditions.add(before.getType(), before.getPlan());
        }

        return this;
    }

    public JPAContext create() {
        Cache.init(database, entityManager);
        PreconditionCache.init(preconditions);

        final MetaModelProvider metaModelProvider = new MetaModelProviderImpl(entityManager);
        final List<Relation> relations = RelationCreator
                .from(metaModelProvider)
                .with(dependencies)
                .with(RelationProviderFactory.getRelationshipProvider(database, entityManager))
                .generate();

        final HierarchyGenerator hierarchyGenerator = new HierarchyGeneratorImpl();
        final HierarchyGraph hierarchyGraph = hierarchyGenerator.generate(relations);

        return JPAContext.newInstance(entityManager, generator, hierarchyGraph);
    }
}

