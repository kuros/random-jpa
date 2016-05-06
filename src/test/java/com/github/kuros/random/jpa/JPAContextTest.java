package com.github.kuros.random.jpa;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.C;
import com.github.kuros.random.jpa.testUtil.entity.D;
import com.github.kuros.random.jpa.testUtil.entity.D_;
import com.github.kuros.random.jpa.testUtil.entity.PrimitiveEntity;
import com.github.kuros.random.jpa.testUtil.entity.PrimitiveEntity_;
import com.github.kuros.random.jpa.testUtil.entity.RelationEntity;
import com.github.kuros.random.jpa.testUtil.entity.RelationManyToOne;
import com.github.kuros.random.jpa.testUtil.entity.RelationOneToMany;
import com.github.kuros.random.jpa.testUtil.entity.RelationOneToOne;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.DependencyHelper;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Entity;
import com.github.kuros.random.jpa.types.Plan;
import com.github.kuros.random.jpa.types.Printer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
public class JPAContextTest {

    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        entityManager = EntityManagerProvider.getEntityManager();
    }

    @Test
    public void shouldCreateAndPersistHierarchyWithDeprecatedMethod() throws Exception {

        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(dependencies)
                .create();

        entityManager.getTransaction().begin();

        final ResultMap resultMap = jpaContext
                .createAndPersist(Plan.of(Entity.of(Z.class)));

        entityManager.getTransaction().commit();
        entityManager.close();

        assertEquals(1, resultMap.getAll(Z.class).size());
        assertEquals(1, resultMap.getAll(X.class).size());
        assertEquals(1, resultMap.getAll(Y.class).size());

        final Z z = resultMap.get(Z.class);
        resultMap.print(new Printer() {
            public void print(final String string) {
                System.out.println(string);
            }
        });

        assertEquals(z.getxId(), resultMap.get(X.class).getId());
        assertEquals(z.getyId(), resultMap.get(Y.class).getId());
    }

    @Test
    public void shouldCreateAndPersistHierarchy() throws Exception {

        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(dependencies)
                .generate();

        entityManager.getTransaction().begin();

        final ResultMap resultMap = jpaContext
                .createAndPersist(Plan.of(Entity.of(Z.class)));

        entityManager.getTransaction().commit();
        entityManager.close();

        assertEquals(1, resultMap.getAll(Z.class).size());
        assertEquals(1, resultMap.getAll(X.class).size());
        assertEquals(1, resultMap.getAll(Y.class).size());

        final Z z = resultMap.get(Z.class);
        resultMap.print(new Printer() {
            public void print(final String string) {
                System.out.println(string);
            }
        });

        assertEquals(z.getxId(), resultMap.get(X.class).getId());
        assertEquals(z.getyId(), resultMap.get(Y.class).getId());
    }

    @Test
    public void shouldCreateAndPersistHierarchyWithSoftLink() throws Exception {

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .generate();

        entityManager.getTransaction().begin();

        final ResultMap resultMap = jpaContext
                .createAndPersist(Plan.of(Entity.of(D.class).withSoftLink(D_.zId, Z_.id)));

        entityManager.getTransaction().commit();
        entityManager.close();

        assertEquals(1, resultMap.getAll(Z.class).size());
        assertEquals(1, resultMap.getAll(D.class).size());

        final D d = resultMap.get(D.class);

        assertEquals(d.getzId(), resultMap.get(Z.class).getId().intValue());
    }

    @Test
    public void shouldCreateMultipleHierarchyAndPersistHierarchy() throws Exception {

        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(dependencies)
                .generate();

        entityManager.getTransaction().begin();

        final ResultMap resultMap = jpaContext
                .createAndPersist(Plan.of(Entity.of(Z.class, 2), Entity.of(X.class).createBefore(Y.class)));

        entityManager.getTransaction().commit();
        entityManager.close();

        assertEquals(2, resultMap.getAll(Z.class).size());
        assertEquals(1, resultMap.getAll(X.class).size());
        assertEquals(1, resultMap.getAll(Y.class).size());

        final Z z = resultMap.get(Z.class);

        assertEquals(z.getxId(), resultMap.get(X.class).getId());
        assertEquals(z.getyId(), resultMap.get(Y.class).getId());

        final Z z2 = resultMap.get(Z.class, 1);

        assertEquals(z2.getxId(), resultMap.get(X.class).getId());
        assertEquals(z2.getyId(), resultMap.get(Y.class).getId());
    }

    @Test
    public void shouldCreateMultipleHierarchyAsPerParentCountAndPersistHierarchy() throws Exception {

        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(dependencies)
                .generate();

        entityManager.getTransaction().begin();

        final ResultMap resultMap = jpaContext
                .createAndPersist(Plan.of(Entity.of(Z.class, 2),
                        Entity.of(X.class).createBefore(Y.class),
                        Entity.of(Y.class, 2)));

        entityManager.getTransaction().commit();
        entityManager.close();

        assertEquals(4, resultMap.getAll(Z.class).size());
        assertEquals(1, resultMap.getAll(X.class).size());
        assertEquals(2, resultMap.getAll(Y.class).size());

        final Z z = resultMap.get(Z.class);
        assertEquals(z.getxId(), resultMap.get(X.class).getId());
        assertEquals(z.getyId(), resultMap.get(Y.class, 0).getId());

        final Z z2 = resultMap.get(Z.class, 1);
        assertEquals(z2.getxId(), resultMap.get(X.class).getId());
        assertEquals(z2.getyId(), resultMap.get(Y.class, 0).getId());

        final Z z3 = resultMap.get(Z.class, 2);
        assertEquals(z3.getxId(), resultMap.get(X.class).getId());
        assertEquals(z3.getyId(), resultMap.get(Y.class, 1).getId());

        final Z z4 = resultMap.get(Z.class, 3);
        assertEquals(z4.getxId(), resultMap.get(X.class).getId());
        assertEquals(z4.getyId(), resultMap.get(Y.class, 1).getId());
    }

    @Test
    public void shouldRemoveEntityWithGivenId() throws Exception {

        final X x = new X();
        EntityManagerProvider.persist(x);

        final String query = "FROM X where id=" + x.getId();
        assertEquals(1, EntityManagerProvider.find(query).size());

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .generate();

        entityManager.getTransaction().begin();
        jpaContext.remove(X.class, x.getId());
        entityManager.getTransaction().commit();


        final List<X> found = EntityManagerProvider.find(query);

        assertEquals(0, found.size());

    }

    @Test
    public void shouldRemoveAllEntityOfGivenType() throws Exception {

        final X x = new X();
        EntityManagerProvider.persist(x);

        final String query = "FROM X";
        assertTrue(EntityManagerProvider.find(query).size() > 0);

        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .generate();

        entityManager.getTransaction().begin();
        jpaContext.remove(X.class);
        entityManager.getTransaction().commit();


        final List<X> found = EntityManagerProvider.find(query);

        assertEquals(0, found.size());

    }

    @Test
    public void shouldRemoveAllEntity() throws Exception {

        final X x = new X();
        EntityManagerProvider.persist(x);

        final Y y = new Y();
        EntityManagerProvider.persist(y);

        assertTrue(EntityManagerProvider.find("FROM X").size() > 0);
        assertTrue(EntityManagerProvider.find("FROM Y").size() > 0);

        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(dependencies)
                .generate();

        entityManager.getTransaction().begin();
        jpaContext.removeAll();
        entityManager.getTransaction().commit();


        assertEquals(0, EntityManagerProvider.find("FROM X").size());
        assertEquals(0, EntityManagerProvider.find("FROM Y").size());

    }

    @Test @Ignore
    public void shouldObjectValueForMappedRelationsInCaseOfOneToMany() throws Exception {
        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(dependencies)
                .generate();


        final CreationPlan creationPlan = jpaContext.create(Plan.of(Entity.of(RelationOneToMany.class)));

        entityManager.getTransaction().begin();
        final ResultMap persist = jpaContext.persist(creationPlan);
        entityManager.getTransaction().commit();



        final RelationOneToOne relationOneToOne = persist.get(RelationOneToOne.class);
        assertNotNull(relationOneToOne);
        final RelationManyToOne relationManyToOne  = persist.get(RelationManyToOne.class);
        assertNotNull(relationManyToOne);

        final RelationEntity relationEntity = persist.get(RelationEntity.class);
        assertNotNull(relationEntity);

        assertEquals(relationEntity.getRelationManyToOne().getId(), relationManyToOne.getId());
        assertEquals(relationEntity.getRelationOneToOne().getId(), relationOneToOne.getId());
        assertNull(relationEntity.getRelationOneToMany());

        final RelationOneToMany relationOneToMany = persist.get(RelationOneToMany.class);
        assertNotNull(relationOneToMany);

        final List<RelationEntity> foundRelationEntities = EntityManagerProvider.find("FROM RelationEntity where id=" + relationEntity.getId());
        assertEquals(1, foundRelationEntities.size());

        final List<RelationOneToMany> foundOneToManies = foundRelationEntities.get(0).getRelationOneToMany();
        assertNotNull(foundOneToManies);
        assertEquals(1, foundOneToManies.size());
        assertEquals(relationOneToMany.getId(), foundOneToManies.get(0).getId());

    }

    @Test
    public void shouldPersistDefaultValuesForPrimitiveTypeForV1() throws Exception {
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .generate();

        final CreationPlan creationPlan = jpaContext.create(Plan.of(Entity.of(PrimitiveEntity.class)));
        // setting default value false to invoke V1 version call
        creationPlan.get(PrimitiveEntity.class).setDefaultBoolean(false);

        final ResultMap persist = jpaContext.persist(creationPlan);
        final PrimitiveEntity primitiveEntity = persist.get(PrimitiveEntity.class);
        assertEquals(false, primitiveEntity.isDefaultBoolean());
        assertEquals((byte) 0, primitiveEntity.getDefaultByte());
        assertEquals((char) 0, primitiveEntity.getDefaultChar());
        assertEquals(0.0, primitiveEntity.getDefaultDouble(), 0.0001);
        assertEquals(0.0f, primitiveEntity.getDefaultFloat(), 0.0001);
        assertEquals(0, primitiveEntity.getDefaultInt());
        assertEquals(0L, primitiveEntity.getDefaultLong());
        assertEquals((short) 0, primitiveEntity.getDefaultShort());

    }

    @Test
    public void shouldPersistRandomValuesForPrimitiveTypeForV2() throws Exception {
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .generate();

        final CreationPlan creationPlan = jpaContext.create(Plan.of(Entity.of(PrimitiveEntity.class)));

        final ResultMap persist = jpaContext.persist(creationPlan);
        final PrimitiveEntity primitiveEntity = persist.get(PrimitiveEntity.class);
        assertNotEquals((byte) 0, primitiveEntity.getDefaultByte());
        assertNotEquals((char) 0, primitiveEntity.getDefaultChar());
        assertNotEquals(0.0, primitiveEntity.getDefaultDouble(), 0.0001);
        assertNotEquals(0.0f, primitiveEntity.getDefaultFloat(), 0.0001);
        assertNotEquals(0, primitiveEntity.getDefaultInt());
        assertNotEquals(0L, primitiveEntity.getDefaultLong());
        assertNotEquals((short) 0, primitiveEntity.getDefaultShort());

    }

    @Test
    public void shouldPersistDefaultValuesForPrimitiveTypeWhenProvided() throws Exception {
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .generate();

        final CreationPlan creationPlan = jpaContext.create(Plan.of(Entity.of(PrimitiveEntity.class)));

        creationPlan.set(PrimitiveEntity_.defaultBoolean, false);
        creationPlan.set(PrimitiveEntity_.defaultByte, (byte) 0);
        creationPlan.set(PrimitiveEntity_.defaultChar, (char) 0);
        creationPlan.set(PrimitiveEntity_.defaultDouble, 0.0);
        creationPlan.set(PrimitiveEntity_.defaultFloat, 0.0F);
        creationPlan.set(PrimitiveEntity_.defaultInt, 0);
        creationPlan.set(PrimitiveEntity_.defaultLong, 0L);
        creationPlan.set(PrimitiveEntity_.defaultShort, (short)0);


        final ResultMap persist = jpaContext.persist(creationPlan);
        final PrimitiveEntity primitiveEntity = persist.get(PrimitiveEntity.class);
        assertEquals(false, primitiveEntity.isDefaultBoolean());
        assertEquals((byte) 0, primitiveEntity.getDefaultByte());
        assertEquals((char) 0, primitiveEntity.getDefaultChar());
        assertEquals(0.0, primitiveEntity.getDefaultDouble(), 0.0001);
        assertEquals(0.0F, primitiveEntity.getDefaultFloat(), 0.0001);
        assertEquals(0, primitiveEntity.getDefaultInt());
        assertEquals(0L, primitiveEntity.getDefaultLong());
        assertEquals((short) 0, primitiveEntity.getDefaultShort());

    }

    @Test
    public void shouldReUseHierarchyIfIdIsProvided() throws Exception {

        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(DependencyHelper.getLinks());
        final JPAContext jpaContext = JPAContextFactory
                .newInstance(Database.NONE, entityManager)
                .with(dependencies)
                .generate();

        entityManager.getTransaction().begin();

        final ResultMap expected = jpaContext
                .createAndPersist(Plan.of(Entity.of(D.class)));

        entityManager.getTransaction().commit();



        entityManager.getTransaction().begin();

        final ResultMap actual = jpaContext
                .createAndPersist(Plan.of(Entity.of(D.class).with(D_.id, expected.get(D.class).getId())));

        entityManager.getTransaction().commit();

        assertEquals(expected.get(D.class).getId(), actual.get(D.class).getId());
        assertEquals(expected.get(C.class).getId(), actual.get(C.class).getId());
        assertEquals(expected.get(B.class).getId(), actual.get(B.class).getId());
        assertEquals(expected.get(A.class).getId(), actual.get(A.class).getId());
    }

    @After
    public void tearDown() throws Exception {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
