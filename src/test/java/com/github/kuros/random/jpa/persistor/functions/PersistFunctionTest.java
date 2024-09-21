package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersistFunctionTest {

    @Mock
    private Cache cache;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cache.getEntityManager()).thenReturn(entityManager);
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldPersistObject() {
        final PersistFunction persistFunction = new PersistFunction(cache);

        final ArgumentCaptor<Z> argumentCaptor = ArgumentCaptor.forClass(Z.class);


        final Z z = RandomFixture.create(Z.class);

        final Z apply = (Z) persistFunction.apply(z);

        assertEquals(z, apply);

        Mockito.verify(entityManager, Mockito.times(1)).persist(argumentCaptor.capture());
        assertEquals(z, argumentCaptor.getValue());
    }

//    @SuppressWarnings("unchecked")
//    @Test
//    public void shouldThrowExceptionIfFailedToPersist() {
//        assertThrows(SQLException.class, () -> {
//            final PersistFunction persistFunction = new PersistFunction(cache);
//
//            final Z z = RandomFixture.create(Z.class);
//            Mockito.doThrow(SQLException.class).when(entityManager).persist(Mockito.eq(z));
//            persistFunction.apply(z);
//        });
//    }
}
