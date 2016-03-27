package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.random.simple.SimpleRandomGenerator;
import com.github.kuros.random.jpa.random.simple.SimpleRandomGeneratorFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class FindByIdTest {

    @Mock
    private Cache cache;

    @Mock
    private AttributeProvider attributeProvider;

    @Mock
    private Finder finder;
    private SimpleRandomGenerator simpleRandomGenerator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cache.getAttributeProvider()).thenReturn(attributeProvider);
        mockEntityTableMapping("id");
        simpleRandomGenerator = SimpleRandomGeneratorFactory.newInstance().create();
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnNullIfDefaultValueForIdIsFound() throws Exception {

        final FindById findById = new FindById(cache);
        findById.setFinder(finder);

        final FindByIdTestClass randomTestClass = simpleRandomGenerator.getRandom(FindByIdTestClass.class);
        randomTestClass.setId(0);

        final Object apply = findById.apply(randomTestClass);

        Assert.assertNull(apply);

    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnInvokeFinderToLoadDataFromDB() throws Exception {

        final FindById findById = new FindById(cache);
        findById.setFinder(finder);

        final FindByIdTestClass randomTestClass = simpleRandomGenerator.getRandom(FindByIdTestClass.class);

        Mockito.when(finder.findByAttributes(Mockito.any(), Mockito.anyList())).thenReturn(randomTestClass);

        final FindByIdTestClass apply = (FindByIdTestClass) findById.apply(randomTestClass);

        Assert.assertEquals(randomTestClass, apply);
        Mockito.verify(finder, Mockito.times(1)).findByAttributes(Mockito.any(), Mockito.anyList());

    }

    @Test(expected = RandomJPAException.class)  @SuppressWarnings("unchecked")
    public void shouldThrowExceptionAttributeIdIsNotFound() throws Exception {
        mockEntityTableMapping("randomAttributeId");
        final FindById findById = new FindById(cache);
        findById.setFinder(finder);

        final FindByIdTestClass randomTestClass = simpleRandomGenerator.getRandom(FindByIdTestClass.class);

        Mockito.when(finder.findByAttributes(Mockito.any(), Mockito.anyList())).thenReturn(randomTestClass);

        findById.apply(randomTestClass);

    }

    private void mockEntityTableMapping(final String id) {
        final EntityTableMapping entityTableMapping = getEntityTableMapping(id);
        Mockito.when(attributeProvider.get(Mockito.eq(FindByIdTestClass.class))).thenReturn(entityTableMapping);
    }

    private EntityTableMapping getEntityTableMapping(final String id) {
        final EntityTableMapping entityTableMapping = new EntityTableMapping(FindByIdTestClass.class);
        entityTableMapping.addAttributeIds(id);
        final String[] strings = {id};
        entityTableMapping.addColumnIds(strings);
        entityTableMapping.addAttributeColumnMapping("anyVariable", "any_column_name");
        return entityTableMapping;
    }

    private class FindByIdTestClass {

        private long id;
        private String anyVariable;

        public long getId() {
            return id;
        }

        public void setId(final long id) {
            this.id = id;
        }
    }
}
