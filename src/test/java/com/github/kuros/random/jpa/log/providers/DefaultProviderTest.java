package com.github.kuros.random.jpa.log.providers;

import com.github.kuros.random.jpa.testUtil.RandomFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.logging.Level;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DefaultProviderTest {

    @Mock
    private java.util.logging.Logger logger;
    private DefaultProvider provider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        provider = new DefaultProvider(logger);
    }

    @Test
    public void shouldLogInfoWithMessageAndArguments() {
        final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final String message = RandomFixture.create(String.class);

        provider.info(message);

        Mockito.verify(logger, Mockito.times(1)).log(levelCaptor.capture(), messageCaptor.capture());
        assertEquals(Level.INFO, levelCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
    }

    @Test
    public void shouldLogInfoWithMessageAndException() {
        final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> throwableArgumentCaptor = ArgumentCaptor.forClass(Throwable.class);
        final String message = RandomFixture.create(String.class);
        final Throwable throwable = new RuntimeException();

        provider.info(message, throwable);

        Mockito.verify(logger, Mockito.times(1)).log(levelCaptor.capture(), messageCaptor.capture(), throwableArgumentCaptor.capture());
        assertEquals(Level.INFO, levelCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(throwable, throwableArgumentCaptor.getValue());
    }



    @Test
    public void shouldLogDebugWithMessageAndArguments() {
        final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final String message = RandomFixture.create(String.class);

        provider.debug(message);

        Mockito.verify(logger, Mockito.times(1)).log(levelCaptor.capture(), messageCaptor.capture());
        assertEquals(Level.FINEST, levelCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
    }

    @Test
    public void shouldLogDebugWithMessageAndException() {
        final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> throwableArgumentCaptor = ArgumentCaptor.forClass(Throwable.class);
        final String message = RandomFixture.create(String.class);
        final Throwable throwable = new RuntimeException();

        provider.debug(message, throwable);

        Mockito.verify(logger, Mockito.times(1)).log(levelCaptor.capture(), messageCaptor.capture(), throwableArgumentCaptor.capture());
        assertEquals(Level.FINEST, levelCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(throwable, throwableArgumentCaptor.getValue());
    }


    @Test
    public void shouldLogErrorWithMessageAndArguments() {
        final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object[]> objectArgumentCaptor = ArgumentCaptor.forClass(Object[].class);
        final String message = RandomFixture.create(String.class);

        final Object[] args = {};
        provider.error(message, args);

        Mockito.verify(logger, Mockito.times(1)).log(levelCaptor.capture(), messageCaptor.capture(), objectArgumentCaptor.capture());
        assertEquals(Level.SEVERE, levelCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
        assertArrayEquals(args, objectArgumentCaptor.getValue());
    }

    @Test
    public void shouldLogErrorWithMessageAndException() {
        final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> throwableArgumentCaptor = ArgumentCaptor.forClass(Throwable.class);
        final String message = RandomFixture.create(String.class);
        final Throwable throwable = new RuntimeException();

        provider.error(message, throwable);

        Mockito.verify(logger, Mockito.times(1)).log(levelCaptor.capture(), messageCaptor.capture(), throwableArgumentCaptor.capture());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(throwable, throwableArgumentCaptor.getValue());
    }

    @Test
    public void shouldLogWarnWithMessageAndArguments() {
        final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final String message = RandomFixture.create(String.class);

        final Object[] args = {};
        provider.warn(message, args);

        Mockito.verify(logger, Mockito.times(1)).log(levelCaptor.capture(), messageCaptor.capture());
        assertEquals(Level.WARNING, levelCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
    }

    @Test
    public void shouldLogWarnWithMessageAndException() {
        final ArgumentCaptor<Level> levelCaptor = ArgumentCaptor.forClass(Level.class);
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> throwableArgumentCaptor = ArgumentCaptor.forClass(Throwable.class);
        final String message = RandomFixture.create(String.class);
        final Throwable throwable = new RuntimeException();

        provider.warn(message, throwable);

        Mockito.verify(logger, Mockito.times(1)).log(levelCaptor.capture(), messageCaptor.capture(), throwableArgumentCaptor.capture());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(throwable, throwableArgumentCaptor.getValue());
    }
}
