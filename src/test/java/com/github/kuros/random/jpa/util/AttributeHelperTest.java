package com.github.kuros.random.jpa.util;

import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.A_;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.B_;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AttributeHelperTest {

    @Test
    public void shouldReturnDeclaringClass() throws Exception {
        EntityManagerProvider.init();

        final Class<?> declaringClass = AttributeHelper.getDeclaringClass(A_.id);
        Assert.assertEquals(A.class, declaringClass);
    }

    @Test
    public void shouldReturnAttributeClass() throws Exception {
        EntityManagerProvider.init();

        final Class<?> attributeClass = AttributeHelper.getAttributeClass(A_.id);
        Assert.assertEquals(Long.TYPE, attributeClass);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenAttributeIsNullAttributeClass() throws Exception {
        final Class<?> attributeClass = AttributeHelper.getAttributeClass(null);
        Assert.assertEquals(Long.TYPE, attributeClass);
    }

    @Test
    public void shouldReturnTheMappedFieldForAttribute() throws Exception {
        EntityManagerProvider.init();

        final Field field = AttributeHelper.getField(A_.id);

        Assert.assertEquals("id", field.getName());
        Assert.assertEquals(Long.TYPE, field.getType());
        Assert.assertEquals(A.class, field.getDeclaringClass());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionWhenAttributeIsNull() throws Exception {
        AttributeHelper.getField(null);
    }

    @Test
    public void shouldReturnNameOfTheField() throws Exception {
        EntityManagerProvider.init();

        final String name = AttributeHelper.getName(A_.id);

        Assert.assertEquals("id", name);
    }

    @Test
    public void shouldReturnNameOfTheMappedMethodField() throws Exception {
        EntityManagerProvider.init();

        final String name = AttributeHelper.getName(A_.address);

        Assert.assertEquals("address", name);
    }

    @Test
    public void shouldReturnListOfFieldsForAtttributes() throws Exception {

        EntityManagerProvider.init();

        final List<Attribute<?, ?>> attributes = new ArrayList<Attribute<?, ?>>();
        attributes.add(A_.id);
        attributes.add(B_.id);

        final List<Field> fields = AttributeHelper.getFields(attributes);

        Assert.assertEquals(2, fields.size());
        Assert.assertEquals("id", fields.get(0).getName());
        Assert.assertEquals(A.class, fields.get(0).getDeclaringClass());

        Assert.assertEquals("id", fields.get(1).getName());
        Assert.assertEquals(B.class, fields.get(1).getDeclaringClass());
    }
}
