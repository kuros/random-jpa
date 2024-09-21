package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.A_;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.B_;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AttributeHelperTest {

    @Test
    public void shouldReturnDeclaringClass() {
        EntityManagerProvider.init();

        final Class<?> declaringClass = AttributeHelper.getDeclaringClass(A_.id);
        Assertions.assertEquals(A.class, declaringClass);
    }

    @Test
    public void shouldReturnAttributeClass() {
        EntityManagerProvider.init();

        final Class<?> attributeClass = AttributeHelper.getAttributeClass(A_.id);
        Assertions.assertEquals(Long.TYPE, attributeClass);
    }

    @Test
    public void shouldThrowExceptionWhenAttributeIsNullAttributeClass() {
        assertThrows(NullPointerException.class, () -> {
            final Class<?> attributeClass = AttributeHelper.getAttributeClass(null);
            Assertions.assertEquals(Long.TYPE, attributeClass);
        });
    }

    @Test
    public void shouldReturnTheMappedFieldForAttribute() {
        EntityManagerProvider.init();

        final Field field = AttributeHelper.getField(A_.id);

        Assertions.assertEquals("id", field.getName());
        Assertions.assertEquals(Long.TYPE, field.getType());
        Assertions.assertEquals(A.class, field.getDeclaringClass());
    }

    @Test
    public void shouldThrowExceptionWhenAttributeIsNull() {
        assertThrows(NullPointerException.class, () ->
            AttributeHelper.getField(null));
    }

    @Test
    public void shouldReturnNameOfTheField() {
        EntityManagerProvider.init();

        final String name = AttributeHelper.getName(A_.id);

        Assertions.assertEquals("id", name);
    }

    @Test
    public void shouldReturnNameOfTheMappedMethodField() {
        EntityManagerProvider.init();

        final String name = AttributeHelper.getName(A_.address);

        Assertions.assertEquals("address", name);
    }

    @Test
    public void shouldReturnListOfFieldsForAttributes() {

        EntityManagerProvider.init();

        final List<Attribute<?, ?>> attributes = new ArrayList<>();
        attributes.add(A_.id);
        attributes.add(B_.id);

        final List<Field> fields = AttributeHelper.getFields(attributes);

        Assertions.assertEquals(2, fields.size());
        Assertions.assertEquals("id", fields.get(0).getName());
        Assertions.assertEquals(A.class, fields.get(0).getDeclaringClass());

        Assertions.assertEquals("id", fields.get(1).getName());
        Assertions.assertEquals(B.class, fields.get(1).getDeclaringClass());
    }
}
