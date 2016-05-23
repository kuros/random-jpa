package com.github.kuros.random.jpa.persistor.hepler;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.P;
import com.github.kuros.random.jpa.testUtil.entity.Q;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FinderTest {

    private Finder finder;
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        entityManager = EntityManagerProvider.getEntityManager();
        final Cache cache = Cache.create(Database.NONE, entityManager);

        finder = new Finder(cache);
    }

    @Test
    public void shouldFindElementByAttributes() throws Exception {
        final P p = new P();
        EntityManagerProvider.persist(p);
        final Q expected = new Q();
        expected.setpId(p.getId());
        EntityManagerProvider.persist(expected);

        final Q input = new Q();
        input.setpId(p.getId());

        final List<String> attributes = new ArrayList<String>();
        attributes.add("pId");
        final Q actual = finder.findByAttributes(input, attributes);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getpId(), actual.getpId());

    }

    @Test(expected = RandomJPAException.class)
    public void shouldThrowExceptionIfAttributeNamesAreIncorrect() throws Exception {
        final P p = new P();
        EntityManagerProvider.persist(p);
        final Q expected = new Q();
        expected.setpId(p.getId());
        EntityManagerProvider.persist(expected);

        final Q input = new Q();
        input.setpId(p.getId());

        final List<String> attributes = new ArrayList<String>();
        attributes.add("randomId");
        finder.findByAttributes(input, attributes);
    }

    @Test
    public void shouldReturnNullIfResultIsNotFound() throws Exception {
        final Q input = new Q();
        input.setpId(RandomFixture.create(Long.class));

        final List<String> attributes = new ArrayList<String>();
        attributes.add("pId");
        assertNull(finder.findByAttributes(input, attributes));
    }

    @Test
    public void shouldReturnNullIfInputIsEmpty() throws Exception {
        final Q input = new Q();

        final List<String> attributes = new ArrayList<String>();
        assertNull(finder.findByAttributes(input, attributes));

        attributes.add("id");
        assertNull(finder.findByAttributes(null, attributes));

        assertNull(finder.findByAttributes(input, null));
    }

    @Test
    public void shouldReturnNullIfAttributeValueIsNull() throws Exception {
        final Q input = new Q();

        final List<String> attributes = new ArrayList<String>();
        attributes.add("pId");
        assertNull(finder.findByAttributes(input, attributes));
    }

    @Test
    public void shouldFindByAttributesByMapValue() throws Exception {
        final P p = new P();
        EntityManagerProvider.persist(p);
        final Q expected = new Q();
        expected.setpId(p.getId());
        EntityManagerProvider.persist(expected);

        final Map<String, Object> attributeValues = new HashMap<String, Object>();
        attributeValues.put("pId", p.getId());

        final List<Q> actual = finder.findByAttributes(Q.class, attributeValues);

        assertEquals(1, actual.size());
        assertEquals(expected.getId(), actual.get(0).getId());
    }

    @Test
    public void shouldReturnEmptyListIfResultNotFoundWithFindByAttributesByMapValue() throws Exception {

        final Map<String, Object> attributeValues = new HashMap<String, Object>();
        attributeValues.put("pId", RandomFixture.create(Long.class));

        final List<Q> actual = finder.findByAttributes(Q.class, attributeValues);

        assertEquals(0, actual.size());
    }

    @Test
    public void shouldReturnNullWithFindByAttributesByMapValueIsEmpty() throws Exception {
        final P p = new P();
        EntityManagerProvider.persist(p);
        final Q expected = new Q();
        expected.setpId(p.getId());
        EntityManagerProvider.persist(expected);

        final Map<String, Object> attributeValues = new HashMap<String, Object>();
        assertEquals(0 , finder.findByAttributes(null, attributeValues).size());
        assertEquals(0 , finder.findByAttributes(Q.class, attributeValues).size());

    }

    @After
    public void tearDown() throws Exception {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
