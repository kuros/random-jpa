package com.github.kuros.random.jpa.random.generator;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;
import com.github.kuros.random.jpa.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RandomGeneratorTest {

    @Mock
    private Cache cache;

    @Mock
    private SQLCharacterLengthProvider sqlCharacterLengthProvider;

    private Generator generator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(cache.getSqlCharacterLengthProvider()).thenReturn(sqlCharacterLengthProvider);

        //Apply no constraintForLength
        Mockito.when(sqlCharacterLengthProvider
                .applyLengthConstraint(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenAnswer(new Answer<Object>() {
                    public Object answer(final InvocationOnMock invocationOnMock) {
                        return invocationOnMock.getArguments()[2];
                    }
                });

        generator = Generator.newInstance();
    }

    @Test
    public void shouldGenerateRandomObjectForClass() throws Exception {
        final RandomGenerator randomGenerator = RandomGenerator.newInstance(cache);
        final Z z = randomGenerator.generateRandom(Z.class);
        assertNotNull(z);
        assertNull(z.getId());
        assertNull(z.getxId());
        assertNull(z.getyId());
    }

    @Test
    public void shouldGenerateRandomObjectForField() throws Exception {
        final RandomGenerator randomGenerator = RandomGenerator.newInstance(cache);
        final Z z = randomGenerator.generateRandom(Z.class);
        assertNotNull(z);
        assertNull(z.getId());
        final Field zId = Util.getField(Z.class, "xId");

        final Object randomActual = randomGenerator.generateRandom(zId);

        assertNotNull(randomActual);
        assertTrue(randomActual instanceof Long);

        Mockito.verify(sqlCharacterLengthProvider, Mockito.times(1)).applyLengthConstraint(Mockito.anyString(), Mockito.anyString(), Mockito.any());
    }

    @Test
    public void shouldUseProvidedRandomClassGeneratorForObjectGenerator() throws Exception {
        final Z expected = RandomFixture.create(Z.class);

        generator.addClassGenerator(new RandomClassGenerator() {
            public Collection<Class<?>> getTypes() {
                final List<Class<?>> classes = new ArrayList<Class<?>>();
                classes.add(Z.class);
                return classes;
            }

            public Object doGenerate(final Class<?> aClass) {
                return expected;
            }
        });

        final RandomGenerator randomGenerator = RandomGenerator.newInstance(cache, generator);

        final Z actual = randomGenerator.generateRandom(Z.class);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldUseProvidedRandomAttributeGeneratorForObjectGenerator() throws Exception {

        EntityManagerProvider.init();

        final Long aLong = RandomFixture.create(Long.class);

        generator.addAttributeGenerator(new RandomAttributeGenerator() {
            public List<? extends Attribute> getAttributes() {
                final List<Attribute> attributes = new ArrayList<Attribute>();
                attributes.add(Z_.xId);
                return attributes;
            }

            public Object doGenerate() {
                return aLong;
            }
        });

        final RandomGenerator randomGenerator = RandomGenerator.newInstance(cache, generator);

        final Field xId = Util.getField(Z.class, "xId");
        final Object actual = randomGenerator.generateRandom(xId);

        assertEquals(aLong, actual);
    }
}
