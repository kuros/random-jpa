package com.github.kuros.random.jpa.log.providers;

import com.github.kuros.random.jpa.testUtil.RandomFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Slf4JProviderTest {

    @Mock
    private org.slf4j.Logger logger;
    private Slf4JProvider provider;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        provider = new Slf4JProvider(logger);
    }

    @Test
    public void shouldLogInfoWithMessageAndArguments() {
        final String message = RandomFixture.create(String.class);
        final Long aLong = RandomFixture.create(Long.class);
        final Integer aInteger = RandomFixture.create(Integer.class);
        final String aString = RandomFixture.create(String.class);

        provider.info(message, aInteger, aLong, aString);

        Mockito.verify(logger, Mockito.times(1)).info(message, aInteger, aLong, aString);
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
        final String s = RandomFixture.create(String.class);
        final Long aLong = RandomFixture.create(Long.class);
        final Integer aInteger = RandomFixture.create(Integer.class);
        final String aString = RandomFixture.create(String.class);

        provider.debug(s, aInteger, aLong, aString);

        Mockito.verify(logger, Mockito.times(1)).debug(s, aInteger, aLong, aString);
    }

    @Test
    public void shouldLogDebugWithMessageAndException() {
        final ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Throwable> throwableArgumentCaptor = ArgumentCaptor.forClass(Throwable.class);
        final String message = RandomFixture.create(String.class);
        final Throwable throwable = new RuntimeException();

        provider.debug(message, throwable);

        Mockito.verify(logger, Mockito.times(1)).debug(messageCaptor.capture(), throwableArgumentCaptor.capture());
        assertEquals(message, messageCaptor.getValue());
        assertEquals(throwable, throwableArgumentCaptor.getValue());
    }


    @Test
    public void shouldLogErrorWithMessageAndArguments() {
        final String s = RandomFixture.create(String.class);
        final Long aLong = RandomFixture.create(Long.class);
        final Integer aInteger = RandomFixture.create(Integer.class);
        final String aString = RandomFixture.create(String.class);

        provider.error(s, aInteger, aLong, aString);

        Mockito.verify(logger, Mockito.times(1)).error(s, aInteger, aLong, aString);
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
