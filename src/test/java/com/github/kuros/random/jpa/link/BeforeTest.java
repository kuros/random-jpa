package com.github.kuros.random.jpa.link;

import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.C;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BeforeTest {

    @Test
    public void shouldGetCorrectTypeAndReferenceClasses() throws Exception {
        final Before before = Before.of(A.class).create(B.class, C.class);
        assertEquals(A.class, before.getType());
        assertEquals(2, before.getToClasses().size());
        assertTrue(before.getToClasses().contains(B.class));
        assertTrue(before.getToClasses().contains(C.class));
    }
}
