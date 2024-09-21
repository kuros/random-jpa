package com.github.kuros.random.jpa.persistor.functions;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.exception.RandomJPAException;
import com.github.kuros.random.jpa.testUtil.RandomFixture;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionProcessorTest {

    @Mock
    private Cache cache;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnListOfFunctionsInOrder() {
        final FunctionProcessor functionProcessor = new FunctionProcessor(cache);
        final List<Function> functions = functionProcessor.getFunctions();

        assertEquals(4, functions.size());
        assertTrue(functions.get(0) instanceof TriggerFunction);
        assertTrue(functions.get(1) instanceof FindByUniqueIdentities);
        assertTrue(functions.get(2) instanceof FindById);
        assertTrue(functions.get(3) instanceof PersistFunction);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldThrowExceptionWhenPersistedObjectIsNull() {
        assertThrows(RandomJPAException.class, () -> {

            final List<Function> functions = new ArrayList<>();

            functions.add(o -> null);

            final Z z = RandomFixture.create(Z.class);

            final TestFunctionProcessor functionProcessor = new TestFunctionProcessor(cache, functions);
            functionProcessor.findOrSave(z);
        });
    }

    @Test @SuppressWarnings("unchecked")
    public void shouldReturnObjectIfTheFunctionIsApplied() {
        final Z z = RandomFixture.create(Z.class);

        final List<Function> functions = new ArrayList<>();

        functions.add(o -> z);




        final TestFunctionProcessor functionProcessor = new TestFunctionProcessor(cache, functions);
        final Z saved = (Z) functionProcessor.findOrSave(z);

        assertEquals(z, saved);
    }

    private class TestFunctionProcessor extends FunctionProcessor {

        private List<Function> functions;

        TestFunctionProcessor(final Cache cache, final List<Function> functions) {
            super(cache);
            this.functions = functions;
        }

        @Override
        List<Function> getFunctions() {
            return functions;
        }
    }
}
