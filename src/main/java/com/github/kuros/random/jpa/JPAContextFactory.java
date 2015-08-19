package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.cache.TriggerCache;
import com.github.kuros.random.jpa.definition.CyclicValidator;
import com.github.kuros.random.jpa.definition.HierarchyGenerator;
import com.github.kuros.random.jpa.definition.HierarchyGeneratorImpl;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.link.Before;
import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.link.Preconditions;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.mapper.RelationCreator;
import com.github.kuros.random.jpa.metamodel.MetaModelProvider;
import com.github.kuros.random.jpa.metamodel.MetaModelProviderImpl;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.types.Trigger;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

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
public final class JPAContextFactory {

    private Database database;
    private EntityManager entityManager;
    private Dependencies dependencies;
    private Generator generator;
    private Preconditions preconditions;
    private List<Trigger<?>> triggers;

    public static JPAContextFactory newInstance(final Database database, final EntityManager entityManager) {
        return new JPAContextFactory(database, entityManager);
    }

    private JPAContextFactory(final Database database, final EntityManager entityManager) {
        this.database = database;
        this.entityManager = entityManager;
        this.preconditions = new Preconditions();
        this.generator = Generator.newInstance();
        this.dependencies = Dependencies.newInstance();
        this.triggers = new ArrayList<Trigger<?>>();
    }

    public JPAContextFactory with(final Dependencies customDependencies) {
        this.dependencies.withLink(customDependencies.getLinks());
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

    public JPAContextFactory withTriggers(final Trigger<?>... triggerTables) {

        for (Trigger<?> triggerTable : triggerTables) {

            if (triggerTable.getLinks().isEmpty()) {
                throw new RandomJPAException("Trigger Not initialised with links: "
                        + triggerTable.getTriggerClass());
            }

            triggers.add(triggerTable);
            dependencies.withLink(triggerTable.getLinks());
        }
        return this;
    }

    public JPAContext create() {
        final Cache cache = Cache
                .create(database, entityManager)
                .with(preconditions);
        TriggerCache.init(triggers);

        final MetaModelProvider metaModelProvider = new MetaModelProviderImpl(cache);
        final List<Relation> relations = RelationCreator
                .from(metaModelProvider)
                .with(dependencies)
                .with(cache.getRelationshipProvider())
                .generate();

        final HierarchyGraph hierarchyGraph = createHierarchyGraph(relations);
        detectCyclicDependency(hierarchyGraph);
        return JPAContext.newInstance(cache, generator, hierarchyGraph);
    }

    private HierarchyGraph createHierarchyGraph(final List<Relation> relations) {
        final HierarchyGenerator hierarchyGenerator = new HierarchyGeneratorImpl();
        return hierarchyGenerator.generate(relations);
    }

    private void detectCyclicDependency(final HierarchyGraph hierarchyGraph) {
        final CyclicValidator cyclicValidator = new CyclicValidator(hierarchyGraph);
        cyclicValidator.validate();
    }
}

