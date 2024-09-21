package com.github.kuros.random.jpa.link;

import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.C;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeforeTest {

    @Test
    public void shouldGetCorrectTypeAndReferenceClasses() {
        final Before before = Before.of(A.class).create(B.class, C.class);
        assertEquals(A.class, before.getType());
        assertEquals(2, before.getToClasses().size());
        assertTrue(before.getToClasses().contains(B.class));
        assertTrue(before.getToClasses().contains(C.class));
    }
}
