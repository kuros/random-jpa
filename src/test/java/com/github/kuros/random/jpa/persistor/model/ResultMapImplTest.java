package com.github.kuros.random.jpa.persistor.model;

import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.types.Printer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ResultMapImplTest {

    @Mock
    private ResultNodeTree resultNodeTree;

    private String constructionTree;

    private X x;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        final List<Object> objectsCreated = new ArrayList<>();
        x =  RandomFixture.create(X.class);
        objectsCreated.add(x);

        final Map<Class<?>, List<Object>> map = new HashMap<>();
        map.put(X.class, objectsCreated);

        Mockito.when(resultNodeTree.getCreatedEntities()).thenReturn(map);

        constructionTree = RandomFixture.create(String.class);

        Mockito.when(resultNodeTree.getConstructionTree()).thenReturn(constructionTree);
    }

    @Test
    public void shouldCreateResultMap() {

        final ResultMap resultMap = ResultMapImpl.newInstance(resultNodeTree);

        final List<X> xList = resultMap.getAll(X.class);
        assertEquals(1, xList.size());
        assertEquals(x, xList.get(0));

        final X xActual = resultMap.get(X.class);
        assertEquals(x, xActual);

        resultMap.print(string -> assertEquals(constructionTree, string));
    }
}
