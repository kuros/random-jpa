package com.kuro.random.jpa.resolver;

import com.kuro.random.jpa.types.AttributeValue;
import com.kuro.random.jpa.types.Plan;
import com.kuro.random.jpa.types.Entity;
import com.kuro.random.jpa.util.AttributeHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/17/15.
 */
public final class EntityResolver {

    private Plan entityList;

    private EntityResolver(final Plan plan) {
        this.entityList = plan;
    }

    public static EntityResolver newInstance(final Plan plan) {
        return new EntityResolver(plan);
    }

    public List<Entity> getEntities() {
        return entityList.getEntities();
    }

    public Map<Field, Object> getFieldValueMap() {
        final Map<Field, Object> fieldValue = new HashMap<Field, Object>();
        final List<Entity> entities = this.entityList.getEntities();

        for (Entity entity : entities) {
            final List<AttributeValue> attributeValues = entity.getAttributeValues();
            for (AttributeValue attributeValue : attributeValues) {
                try {
                    fieldValue.put(AttributeHelper.getField(attributeValue.getAttribute()), attributeValue.getValue());
                } catch (final NoSuchFieldException e) {
                }
            }
        }

        return fieldValue;
    }
}
