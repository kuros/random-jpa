package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.provider.MultiplePrimaryKeyProvider;
import com.github.kuros.random.jpa.provider.UniqueConstraintProvider;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FindByUniqueIdentitiesTest {

    @Mock private Cache cache;

    @Mock private UniqueConstraintProvider uniqueConstraintProvider;

    @Mock private MultiplePrimaryKeyProvider multiplePrimaryKeyProvider;

    @Mock private Finder finder;

    private FindByUniqueIdentities unit;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Mockito.when(cache.getMultiplePrimaryKeyProvider()).thenReturn(multiplePrimaryKeyProvider);
        Mockito.when(cache.getUniqueConstraintProvider()).thenReturn(uniqueConstraintProvider);

        unit = new FindByUniqueIdentities(cache);
        unit.setFinder(finder);
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnObjectIfFoundByMultiplePrimaryKey() {
        final FindByUniqueIdentitiesTestClass testObject = RandomFixture.create(FindByUniqueIdentitiesTestClass.class);

        final List<String> attributes = new ArrayList<>();
        attributes.add("primaryKey1");
        attributes.add("primaryKey2");
        Mockito.when(multiplePrimaryKeyProvider.getMultiplePrimaryKeyAttributes(Mockito.eq(FindByUniqueIdentitiesTestClass.class))).thenReturn(attributes);
        Mockito.when(uniqueConstraintProvider.getUniqueCombinationAttributes(Mockito.eq(FindByUniqueIdentitiesTestClass.class))).thenReturn(null);

        Mockito.when(finder.findByAttributes(Mockito.eq(testObject), Mockito.eq(attributes))).thenReturn(testObject);
        final FindByUniqueIdentitiesTestClass apply = (FindByUniqueIdentitiesTestClass) unit.apply(testObject);
        assertEquals(testObject, apply);
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnObjectIfFoundByUniqueKey() {
        final FindByUniqueIdentitiesTestClass testObject = RandomFixture.create(FindByUniqueIdentitiesTestClass.class);

        final List<String> attributes = new ArrayList<>();
        attributes.add("uniqueKey1");
        attributes.add("uniqueKey2");
        Mockito.when(uniqueConstraintProvider.getUniqueCombinationAttributes(Mockito.eq(FindByUniqueIdentitiesTestClass.class))).thenReturn(attributes);
        Mockito.when(multiplePrimaryKeyProvider.getMultiplePrimaryKeyAttributes(Mockito.eq(FindByUniqueIdentitiesTestClass.class))).thenReturn(null);

        Mockito.when(finder.findByAttributes(Mockito.eq(testObject), Mockito.eq(attributes))).thenReturn(testObject);
        final FindByUniqueIdentitiesTestClass apply = (FindByUniqueIdentitiesTestClass) unit.apply(testObject);
        assertEquals(testObject, apply);
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnNullIfFinderDoesNotFindsObject() {
        final FindByUniqueIdentitiesTestClass testObject = RandomFixture.create(FindByUniqueIdentitiesTestClass.class);

        final List<String> attributes = new ArrayList<>();
        attributes.add("uniqueKey1");
        attributes.add("uniqueKey2");
        Mockito.when(uniqueConstraintProvider.getUniqueCombinationAttributes(Mockito.eq(FindByUniqueIdentitiesTestClass.class))).thenReturn(attributes);
        Mockito.when(multiplePrimaryKeyProvider.getMultiplePrimaryKeyAttributes(Mockito.eq(FindByUniqueIdentitiesTestClass.class))).thenReturn(attributes);

        Mockito.when(finder.findByAttributes(Mockito.eq(testObject), Mockito.eq(attributes))).thenReturn(null);
        final FindByUniqueIdentitiesTestClass apply = (FindByUniqueIdentitiesTestClass) unit.apply(testObject);
        assertNull(apply);
    }

    @SuppressWarnings("unused")
    private class FindByUniqueIdentitiesTestClass {
        private int primaryKey1;
        private long primaryKey2;
        private String uniqueKey1;
        private String uniqueKey2;
    }
}
