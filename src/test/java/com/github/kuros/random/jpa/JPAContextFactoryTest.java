package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.context.BaseContext;
import com.github.kuros.random.jpa.context.JPAContextImpl;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.link.Before;
import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.random.generator.Generator;
import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;
import com.github.kuros.random.jpa.random.generator.RandomGenerator;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.D;
import com.github.kuros.random.jpa.testUtil.entity.D_;
import com.github.kuros.random.jpa.testUtil.entity.P;
import com.github.kuros.random.jpa.testUtil.entity.Q;
import com.github.kuros.random.jpa.testUtil.entity.R;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.DependencyHelper;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.types.Trigger;
import org.junit.After;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JPAContextFactoryTest {

    private EntityManager entityManager;

    @org.junit.Before
    public void setUp() throws Exception {
        entityManager = EntityManagerProvider.getEntityManager();
    }

    @Test
    public void generateWithJpaContextV2() throws Exception {
        final Dependencies customDependencies = Dependencies.newInstance();
        customDependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(customDependencies)
                .generate();

        assertTrue(jpaContext instanceof JPAContextImpl);
    }

    @Test
    public void createJpaContext() throws Exception {
        final Dependencies customDependencies = Dependencies.newInstance();
        customDependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(customDependencies)
                .generate();

        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final HierarchyGraph hierarchyGraph = cache.getHierarchyGraph();

        assertTrue(hierarchyGraph.getParents(Z.class).contains(X.class));
        assertTrue(hierarchyGraph.getParents(Z.class).contains(Y.class));

        assertTrue(hierarchyGraph.getParents(Q.class).contains(P.class));
        assertTrue(hierarchyGraph.getParents(R.class).contains(P.class));
    }

    @Test
    public void createJpaContextWithGenerator() throws Exception {

        final Z z = new Z();

        final Generator generator = Generator.newInstance();
        generator.addClassGenerator(new RandomClassGenerator() {
            public Collection<Class<?>> getTypes() {
                final List<Class<?>> classes = new ArrayList<Class<?>>();
                classes.add(Z.class);
                return classes;
            }

            public Object doGenerate(final Class<?> aClass) {
                return z;
            }
        });

        final Dependencies customDependencies = Dependencies.newInstance();
        customDependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(customDependencies)
                .with(generator)
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final RandomGenerator randomGenerator = jpaContextV2.getGenerator();
        final Z random = randomGenerator.generateRandom(Z.class);

        assertEquals(z, random);
    }

    @Test
    public void createJpaContextWithoutPreConditions() throws Exception {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final HierarchyGraph hierarchyGraph = cache.getHierarchyGraph();

        assertFalse(hierarchyGraph.getParents(D.class).contains(Z.class));

    }

    @Test
    public void createJpaContextWithPreConditions() throws Exception {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .withPreconditions(Before.of(D.class).create(Z.class))
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final HierarchyGraph hierarchyGraph = cache.getHierarchyGraph();

        assertTrue(hierarchyGraph.getParents(D.class).contains(Z.class));

    }

    @Test
    public void createJpaContextWithTriggerTables() throws Exception {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .withTriggers(Trigger.of(D.class).withLink(D_.zId, Z_.id))
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final Trigger<?> trigger = cache.getTriggerCache().getTrigger(D.class);

        assertNotNull(trigger);
        assertEquals(D.class, trigger.getTriggerClass());

        final HierarchyGraph hierarchyGraph = cache.getHierarchyGraph();
        assertTrue(hierarchyGraph.getParents(D.class).contains(Z.class));

    }

    @Test(expected = RandomJPAException.class)
    public void throwExceptionWhenTriggersNotInitializedProperly() throws Exception {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .withTriggers(Trigger.of(D.class))
                .generate();

    }

    @Test
    public void createJpaContextWithSkipTruncation() throws Exception {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .withSkipTruncation(Z.class)
                .generate();


        assertTrue(jpaContext instanceof JPAContextImpl);
        final JPAContextImpl jpaContextV2 = (JPAContextImpl) jpaContext;

        final Cache cache = jpaContextV2.getCache();
        final Set<Class<?>> skipTruncation = cache.getSkipTruncation();
        assertEquals(1, skipTruncation.size());
        assertTrue(skipTruncation.contains(Z.class));
    }

    @Test(expected = RandomJPAException.class)
    public void validateCyclicDependency() throws Exception {

        JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .withPreconditions(Before.of(D.class).create(Z.class), Before.of(Z.class).create(D.class))
                .generate();
    }


    public class TestClass extends BaseContext {

        public TestClass(final Cache cache, final Generator generator) {
            super(cache, generator);
        }

        public CreationPlan create(final Plan plan) {
            return null;
        }
    }

    @After
    public void tearDown() throws Exception {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
