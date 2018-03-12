package com.github.kuros.random.jpa.cleanup;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.P;
import com.github.kuros.random.jpa.testUtil.entity.Q;
import com.github.kuros.random.jpa.testUtil.entity.R;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.MockedHierarchyGraph;
import com.github.kuros.random.jpa.types.DeletionOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CleanerImplTest {

    private Cache cache;
    private EntityManager entityManager;
    private Cleaner cleaner;

    @Before
    public void setUp() throws Exception {
        this.entityManager = EntityManagerProvider.getEntityManager();
        cache = Cache.create(Database.NONE, entityManager);
        cache.with(MockedHierarchyGraph.getHierarchyGraph());

        cleaner = CleanerImpl.newInstance(cache);
    }

    @Test
    public void shouldDeleteRowsWithIdsExceptParallelClasses() throws Exception {

        final X x = getX(null);

        final Y y = getY();

        final Z z = getZ(x, y);

        entityManager.getTransaction().begin();

        cleaner.delete(X.class, x.getId());

        entityManager.getTransaction().commit();

        assertNull(entityManager.find(X.class, x.getId()));
        assertNull(entityManager.find(Z.class, z.getId()));
        assertNotNull(entityManager.find(Y.class, y.getId()));

    }

    @Test @Ignore
    public void shouldDeleteRowsWithIdsExceptParallelClassesByDeletionOrder() throws Exception {

        final X x = getX(null);

        final Y y = getY();

        final Z z = getZ(x, y);

        entityManager.getTransaction().begin();

        final DeletionOrder deletionOrder =
                cleaner.getDeletionOrder(X.class, x.getId());
        cleaner.delete(deletionOrder);

        entityManager.flush();
        entityManager.getTransaction().commit();

        entityManager = EntityManagerProvider.getEntityManager();

        assertNull(entityManager.find(X.class, x.getId()));
        assertNull(entityManager.find(Z.class, z.getId()));
        assertNotNull(entityManager.find(Y.class, y.getId()));

    }

    @Test
    public void shouldNotDeleteRowsWithChildClassIsSkipped() throws Exception {

        final X x = getX(null);

        final Y y = getY();

        final Z z = getZ(x, y);

        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Z.class);
        cache.withSkipTruncations(classes);
        cleaner = CleanerImpl.newInstance(cache);

        entityManager.getTransaction().begin();

        cleaner.delete(X.class, x.getId());

        entityManager.getTransaction().commit();

        assertNotNull(entityManager.find(X.class, x.getId()));
        assertNotNull(entityManager.find(Z.class, z.getId()));
        assertNotNull(entityManager.find(Y.class, y.getId()));

    }

    @Test
    public void shouldDeleteRowsWithParentClassIsSkipped() throws Exception {

        final P p = getP();

        final Q q = getQ(p);

        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(P.class);
        cache.withSkipTruncations(classes);
        cleaner = CleanerImpl.newInstance(cache);

        entityManager.getTransaction().begin();

        cleaner.delete(Q.class, q.getId());

        entityManager.getTransaction().commit();

        assertNotNull(entityManager.find(P.class, p.getId()));
        assertNull(entityManager.find(Q.class, q.getId()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfEntityWithIdNotFound() throws Exception {
        entityManager.getTransaction().begin();
        cleaner.delete(Q.class, RandomFixture.create(Long.class));
        entityManager.getTransaction().commit();
    }


    @Test @SuppressWarnings("unchecked")
    public void shouldTruncateLeafNode() throws Exception {
        final P p = getP();
        final Q q = getQ(p);

        entityManager.getTransaction().begin();
        cleaner.truncate(Q.class);
        entityManager.getTransaction().commit();

        final List<Q> resultList = entityManager.createQuery("FROM Q").getResultList();
        assertEquals(0, resultList.size());
        assertNotNull(entityManager.find(P.class, p.getId()));
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldTruncateAllChildNodes() throws Exception {
        final P p = getP();
        final Q q = getQ(p);
        final R r = getR(p);

        entityManager.getTransaction().begin();
        cleaner.truncate(P.class);
        entityManager.getTransaction().commit();

        final List<Q> resultListQ = entityManager.createQuery("FROM Q").getResultList();
        assertEquals(0, resultListQ.size());

        final List<R> resultListR = entityManager.createQuery("FROM R").getResultList();
        assertEquals(0, resultListR.size());

        final List<P> resultListP = entityManager.createQuery("FROM P").getResultList();
        assertEquals(0, resultListP.size());
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldNotTruncateParallelNodes() throws Exception {
        final X x = getX(null);
        final Y y = getY();
        final Z r = getZ(x, y);

        entityManager.getTransaction().begin();
        cleaner.truncate(X.class);
        entityManager.getTransaction().commit();

        final List<X> resultListX = entityManager.createQuery("FROM X").getResultList();
        assertEquals(0, resultListX.size());

        final List<Z> resultListZ = entityManager.createQuery("FROM Z").getResultList();
        assertEquals(0, resultListZ.size());

        assertNotNull(entityManager.find(Y.class, y.getId()));
    }

    @Test
    public void shouldSkipTruncateIfParentNodeIsSkipped() throws Exception {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Z.class);
        cache.withSkipTruncations(classes);
        cleaner = CleanerImpl.newInstance(cache);


        final X x = getX(null);
        final Y y = getY();
        final Z z = getZ(x, y);

        entityManager.getTransaction().begin();
        cleaner.truncate(X.class);
        cleaner.truncate(Y.class);
        cleaner.truncate(Z.class);
        entityManager.getTransaction().commit();

        assertNotNull(entityManager.find(X.class, x.getId()));
        assertNotNull(entityManager.find(Y.class, y.getId()));
        assertNotNull(entityManager.find(Z.class, z.getId()));
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldTruncateAllTables() throws Exception {
        final X y = getX(null);
        final P p = getP();
        final Q q = getQ(p);

        entityManager.getTransaction().begin();
        cleaner.truncateAll();
        entityManager.getTransaction().commit();

        final List<Q> resultListQ = entityManager.createQuery("FROM Q").getResultList();
        assertEquals(0, resultListQ.size());

        final List<P> resultListP = entityManager.createQuery("FROM P").getResultList();
        assertEquals(0, resultListP.size());

        final List<X> resultListX = entityManager.createQuery("FROM X").getResultList();
        assertEquals(0, resultListX.size());

    }

    private R getR(final P p) {
        final R r = new R();
        r.setpId(p.getId());
        EntityManagerProvider.persist(r);

        return r;
    }

    private Q getQ(final P p) {
        final Q q = new Q();
        q.setpId(p.getId());
        EntityManagerProvider.persist(q);

        return q;
    }

    private P getP() {
        final P p = new P();
        EntityManagerProvider.persist(p);
        return p;
    }


    private Z getZ(final X x, final Y y) {
        final Z z = new Z();
        z.setId(null);
        z.setxId(x.getId());
        z.setyId(y.getId());
        EntityManagerProvider.persist(z);
        return z;
    }

    private Y getY() {
        final Y y = new Y();
        y.setId(null);
        EntityManagerProvider.persist(y);
        return y;
    }

    private X getX(final Long aId) {
        final X x = new X();
        x.setId(null);
        x.setaId(aId);
        EntityManagerProvider.persist(x);
        return x;
    }

    @After
    public void tearDown() throws Exception {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
