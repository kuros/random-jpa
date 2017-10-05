package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.providers.Provider;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;

public class AttributeProviderTest {

    @Mock
    private Provider provider;

    private AttributeProvider attributeProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        attributeProvider = new AttributeProvider(provider);
    }

    @Test
    public void shouldGetEntityTableMappingByType() throws Exception {
        final EntityTableMapping expected = new EntityTableMapping(Object.class);

        Mockito.when(attributeProvider.get(Mockito.any(Class.class))).thenReturn(expected);

        final EntityTableMapping actual = attributeProvider.get(Object.class);

        final ArgumentCaptor<Class> classArgumentCaptor = ArgumentCaptor.forClass(Class.class);
        assertSame(expected, actual);
        Mockito.verify(provider, Mockito.times(1)).get(classArgumentCaptor.capture());
    }

    @Test
    public void shouldGetEntitiesTableMappingByName() throws Exception {
        final EntityTableMapping expected = new EntityTableMapping(Object.class);
        final List<EntityTableMapping> entityTableMappings = new ArrayList<EntityTableMapping>();
        entityTableMappings.add(expected);

        Mockito.when(attributeProvider.get(Mockito.anyString())).thenReturn(entityTableMappings);

        final List<EntityTableMapping> actual = attributeProvider.get(RandomFixture.create(String.class));

        final ArgumentCaptor<String> classArgumentCaptor = ArgumentCaptor.forClass(String.class);
        assertSame(entityTableMappings, actual);
        Mockito.verify(provider, Mockito.times(1)).get(classArgumentCaptor.capture());
    }
}
