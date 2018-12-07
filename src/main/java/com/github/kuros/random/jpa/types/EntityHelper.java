package com.github.kuros.random.jpa.types;

import com.github.kuros.random.jpa.link.Link;

import java.util.List;

public class EntityHelper {

    public static <T> Class<T> getType(final Entity<T> entity) {
        return entity.getType();
    }

    public static <T> int getCount(final Entity<T> entity) {
        return entity.getCount();
    }

    public static <T> List<Link> getSoftLinks(final Entity<T> entity) {
        return entity.getSoftLinks();
    }

    public static <T> List<Class<?>> getAfterClasses(final Entity<T> entity) {
        return entity.getAfterClasses();
    }

    public static <T> List<Class<?>> getBeforeClasses(final Entity<T> entity) {
        return entity.getBeforeClasses();
    }

    public static <T> List<AttributeValue> getAttributeValues(final Entity<T> entity) {
        return entity.getAttributeValues();
    }

    public static <T> List<AttributeIndexValue<T, ?>> getAttributeIndexValues(final Entity<T> entity) {
        return entity.getAttributeIndexValues();
    }

    public static <T> List<ClassIndex> getClassIndices(final Entity<T> entity) {
        return entity.getClassIndices();
    }
}

