package com.github.kuros.random.jpa.persistor.hepler;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.exception.ResultNotFoundException;
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
import static org.junit.Assert.fail;

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
    public void shouldFindElementByAttributes() {
        final P p = new P();
        EntityManagerProvider.persist(p);
        final Q expected = new Q();
        expected.setpId(p.getId());
        EntityManagerProvider.persist(expected);

        final Q input = new Q();
        input.setpId(p.getId());

        final List<String> attributes = new ArrayList<>();
        attributes.add("pId");
        final Q actual = finder.findByAttributes(input, attributes);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getpId(), actual.getpId());

    }

    @Test(expected = RandomJPAException.class)
    public void shouldThrowExceptionIfAttributeNamesAreIncorrect() {
        final P p = new P();
        EntityManagerProvider.persist(p);
        final Q expected = new Q();
        expected.setpId(p.getId());
        EntityManagerProvider.persist(expected);

        final Q input = new Q();
        input.setpId(p.getId());

        final List<String> attributes = new ArrayList<>();
        attributes.add("randomId");
        finder.findByAttributes(input, attributes);
    }

    @Test(expected = ResultNotFoundException.class)
    public void shouldReturnNullIfResultIsNotFound() {
        final Q input = new Q();
        input.setpId(RandomFixture.create(Long.class));

        final List<String> attributes = new ArrayList<>();
        attributes.add("pId");
        finder.findByAttributes(input, attributes);
    }

    @Test
    public void shouldReturnNullIfInputIsEmpty() {
        final Q input = new Q();

        final List<String> attributes = new ArrayList<>();

        try {
            finder.findByAttributes(input, attributes);
            fail();
        } catch (final ResultNotFoundException e) {
            // success
        }

        attributes.add("id");


        try {
            finder.findByAttributes(null, attributes);
            fail();
        } catch (final ResultNotFoundException e) {
            // success
        }


        try {
            finder.findByAttributes(input, null);
            fail();
        } catch (final ResultNotFoundException e) {
            //success
        }
    }

    @Test(expected = RandomJPAException.class)
    public void shouldReturnNullIfAttributeValueIsNull() {
        final Q input = new Q();

        final List<String> attributes = new ArrayList<>();
        attributes.add("pId");
        finder.findByAttributes(input, attributes);
    }

    @Test
    public void shouldFindByAttributesByMapValue() {
        final P p = new P();
        EntityManagerProvider.persist(p);
        final Q expected = new Q();
        expected.setpId(p.getId());
        EntityManagerProvider.persist(expected);

        final Map<String, Object> attributeValues = new HashMap<>();
        attributeValues.put("pId", p.getId());

        final List<Q> actual = finder.findByAttributes(Q.class, attributeValues);

        assertEquals(1, actual.size());
        assertEquals(expected.getId(), actual.get(0).getId());
    }

    @Test
    public void shouldReturnEmptyListIfResultNotFoundWithFindByAttributesByMapValue() {

        final Map<String, Object> attributeValues = new HashMap<>();
        attributeValues.put("pId", RandomFixture.create(Long.class));

        final List<Q> actual = finder.findByAttributes(Q.class, attributeValues);

        assertEquals(0, actual.size());
    }

    @Test
    public void shouldReturnNullWithFindByAttributesByMapValueIsEmpty() {
        final P p = new P();
        EntityManagerProvider.persist(p);
        final Q expected = new Q();
        expected.setpId(p.getId());
        EntityManagerProvider.persist(expected);

        final Map<String, Object> attributeValues = new HashMap<>();
        assertEquals(0 , finder.findByAttributes(null, attributeValues).size());
        assertEquals(0 , finder.findByAttributes(Q.class, attributeValues).size());

    }

    @After
    public void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
