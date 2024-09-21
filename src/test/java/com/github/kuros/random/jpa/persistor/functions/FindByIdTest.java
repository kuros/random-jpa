package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.ColumnNameType;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.random.simple.SimpleRandomGenerator;
import com.github.kuros.random.jpa.random.simple.SimpleRandomGeneratorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FindByIdTest {

    @Mock
    private Cache cache;

    @Mock
    private AttributeProvider attributeProvider;

    @Mock
    private Finder finder;
    private SimpleRandomGenerator simpleRandomGenerator;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cache.getAttributeProvider()).thenReturn(attributeProvider);
        mockEntityTableMapping("id");
        simpleRandomGenerator = SimpleRandomGeneratorFactory.newInstance().create();
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnNullIfDefaultValueForIdIsFound() {

        final FindById findById = new FindById(cache);
        findById.setFinder(finder);

        final FindByIdTestClass randomTestClass = simpleRandomGenerator.getRandom(FindByIdTestClass.class);
        randomTestClass.setId(0);

        final Object apply = findById.apply(randomTestClass);

        Assertions.assertNull(apply);

    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnInvokeFinderToLoadDataFromDB() {

        final FindById findById = new FindById(cache);
        findById.setFinder(finder);

        final FindByIdTestClass randomTestClass = simpleRandomGenerator.getRandom(FindByIdTestClass.class);

        Mockito.when(finder.findByAttributes(Mockito.any(), Mockito.anyList())).thenReturn(randomTestClass);

        final FindByIdTestClass apply = (FindByIdTestClass) findById.apply(randomTestClass);

        Assertions.assertEquals(randomTestClass, apply);
        Mockito.verify(finder, Mockito.times(1)).findByAttributes(Mockito.any(), Mockito.anyList());

    }

    @Test  @SuppressWarnings("unchecked")
    public void shouldThrowExceptionAttributeIdIsNotFound() {
        assertThrows(RandomJPAException.class, () -> {
            mockEntityTableMapping("randomAttributeId");
            final FindById findById = new FindById(cache);
            findById.setFinder(finder);

            final FindByIdTestClass randomTestClass = simpleRandomGenerator.getRandom(FindByIdTestClass.class);

            Mockito.when(finder.findByAttributes(Mockito.any(), Mockito.anyList())).thenReturn(randomTestClass);

            findById.apply(randomTestClass);

        });

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
        entityTableMapping.addAttributeColumnMapping("anyVariable", new ColumnNameType("any_column_name", ColumnNameType.Type.BASIC));
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
