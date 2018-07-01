package com.github.kuros.random.jpa.log.providers;

import com.github.kuros.random.jpa.testUtil.RandomFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class Log4JProviderTest {

    @Mock
    private org.apache.log4j.Logger logger;
    private Log4JProvider provider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        provider = new Log4JProvider(logger);
    }

    @Test
    public void shouldLogInfoWithMessageAndArguments() {
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final String message = RandomFixture.create(String.class);
        final Long aLong = RandomFixture.create(Long.class);
        final Integer aInteger = RandomFixture.create(Integer.class);
        final String aString = RandomFixture.create(String.class);

        final Object[] args = {aInteger, aLong, aString};
        provider.info(message, args);

        Mockito.verify(logger, Mockito.times(1)).info(messageCaptor.capture());
        assertEquals(message, messageCaptor.getValue());
    }

    @Test
    public void shouldLogInfoWithMessageAndException() {
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> throwableArgumentCaptor = ArgumentCaptor.forClass(Throwable.class);
        final String message = RandomFixture.create(String.class);
        final Throwable throwable = new RuntimeException();

        provider.info(message, throwable);

        Mockito.verify(logger, Mockito.times(1)).info(messageCaptor.capture(), throwableArgumentCaptor.capture());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(throwable, throwableArgumentCaptor.getValue());
    }



    @Test
    public void shouldLogDebugWithMessageAndArguments() {
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final String s = RandomFixture.create(String.class);
        final Long aLong = RandomFixture.create(Long.class);
        final Integer aInteger = RandomFixture.create(Integer.class);
        final String aString = RandomFixture.create(String.class);

        final Object[] args = {aInteger, aLong, aString};
        provider.debug(s, args);

        Mockito.verify(logger, Mockito.times(1)).debug(messageCaptor.capture());
        assertEquals(s, messageCaptor.getValue());
    }

    @Test
    public void shouldLogDebugWithMessageAndException() {
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> throwableArgumentCaptor = ArgumentCaptor.forClass(Throwable.class);
        final String message = RandomFixture.create(String.class);
        final Throwable throwable = new RuntimeException();

        provider.debug(message, throwable);

        Mockito.verify(logger, Mockito.times(1)).info(messageCaptor.capture(), throwableArgumentCaptor.capture());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(throwable, throwableArgumentCaptor.getValue());
    }


    @Test
    public void shouldLogErrorWithMessageAndArguments() {
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final String s = RandomFixture.create(String.class);
        final Long aLong = RandomFixture.create(Long.class);
        final Integer aInteger = RandomFixture.create(Integer.class);
        final String aString = RandomFixture.create(String.class);

        final Object[] args = {aInteger, aLong, aString};
        provider.error(s, args);

        Mockito.verify(logger, Mockito.times(1)).error(messageCaptor.capture());
        assertEquals(s, messageCaptor.getValue());
    }

    @Test
    public void shouldLogErrorWithMessageAndException() {
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> throwableArgumentCaptor = ArgumentCaptor.forClass(Throwable.class);
        final String message = RandomFixture.create(String.class);
        final Throwable throwable = new RuntimeException();

        provider.error(message, throwable);

        Mockito.verify(logger, Mockito.times(1)).error(messageCaptor.capture(), throwableArgumentCaptor.capture());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(throwable, throwableArgumentCaptor.getValue());
    }
}
