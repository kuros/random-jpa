package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.types.ClassIndex;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.CreationPlanImpl;
import com.github.kuros.random.jpa.types.FieldIndexValue;
import com.github.kuros.random.jpa.util.ArrayListMultimap;
import com.github.kuros.random.jpa.util.Multimap;
import com.github.kuros.random.jpa.util.NodeHelper;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PersistedEntityResolverImpl implements PersistedEntityResolver {

    private final EntityManager entityManager;
    private final AttributeProvider attributeProvider;
    private final HierarchyGraph hierarchyGraph;
    private Finder finder;

    public PersistedEntityResolverImpl(final Cache cache) {
        this.entityManager = cache.getEntityManager();
        this.attributeProvider = cache.getAttributeProvider();
        this.hierarchyGraph = cache.getHierarchyGraph();
        this.finder = new Finder(cache);
    }

    public Map<ClassIndex, Object> loadPersistedObjectByIds(final CreationPlan creationPlan) {

        final Map<ClassIndex, Object> classIndexObjectMap = new HashMap<ClassIndex, Object>();
        final CreationPlanImpl plan = (CreationPlanImpl) creationPlan;
        final List<FieldIndexValue> fieldIndexValues = plan.getFieldIndexValues();

        for (FieldIndexValue fieldIndexValue : fieldIndexValues) {

            if (shouldGenerateParents(plan, fieldIndexValue)) {
                final Field field = fieldIndexValue.getField();
                final Map<Class, Integer> classIndexes = getClassIndicesMap(plan, fieldIndexValue, field);

                final Object value = fieldIndexValue.getValue();
                if (value != null) {
                    final Object byId = findById(fieldIndexValue.getField().getDeclaringClass(), value);
                    if (byId == null) {
                        throw new IllegalArgumentException("Element not found with id: " + value
                                + ", Class: " + field.getDeclaringClass());
                    }

                    classIndexObjectMap.put(ClassIndex.newInstance(field.getDeclaringClass(), fieldIndexValue.getIndex()), byId);
                    loadParentDetails(classIndexes, classIndexObjectMap, field.getDeclaringClass(), byId);
                }
            }

        }
        return classIndexObjectMap;
    }

    private boolean shouldGenerateParents(final CreationPlanImpl plan, final FieldIndexValue fieldIndexValue) {
        final Randomize randomize = plan.getRandomize();
        final Field declaredField = fieldIndexValue.getField();

        final EntityTableMapping entityTableMapping = attributeProvider.get(declaredField.getDeclaringClass());
        final Set<Relation> relations = hierarchyGraph.getAttributeRelations(declaredField.getDeclaringClass());

        if ((entityTableMapping != null && !entityTableMapping.getAttributeIds().contains(declaredField.getName()))
                || relations == null) {
            if (fieldIndexValue.getIndex() == PersistedEntityResolver.DEFAULT_INDEX) {
                randomize.addFieldValue(fieldIndexValue.getField(), fieldIndexValue.getValue());
            } else {
                randomize.addFieldValue(fieldIndexValue.getField(), fieldIndexValue.getIndex(), fieldIndexValue.getValue());
            }
            return false;
        }
        return true;
    }

    private Map<Class, Integer> getClassIndicesMap(final CreationPlanImpl plan, final FieldIndexValue fieldIndexValue, final Field field) {

        final Integer index = fieldIndexValue.getIndex() == PersistedEntityResolver.DEFAULT_INDEX ? 0 : fieldIndexValue.getIndex();
        final List<ClassIndex> classIndexInOrder = NodeHelper.getClassIndexInOrder(plan.getRoot(),
                field.getDeclaringClass(),
                index);

        final Map<Class, Integer> classIndexMap = new HashMap<Class, Integer>();
        for (ClassIndex classIndex : classIndexInOrder) {
            if (fieldIndexValue.getIndex() == PersistedEntityResolver.DEFAULT_INDEX) {
                classIndexMap.put(classIndex.getType(), PersistedEntityResolver.DEFAULT_INDEX);
            } else {
                classIndexMap.put(classIndex.getType(), classIndex.getIndex());
            }
        }
        return classIndexMap;
    }


    private void loadParentDetails(final Map<Class, Integer> classIndexes, final Map<ClassIndex, Object> classIndexObjectMap, final Class<?> type, final Object object) {

        final Set<Relation> relations = hierarchyGraph.getAttributeRelations(type);
        if (relations == null || relations.isEmpty()) {
            return;
        }

        final Multimap<Class, FieldValue> multimap = ArrayListMultimap.newArrayListMultimap();

        for (Relation relation : relations) {
            final FieldWrapper from = relation.getFrom();
            final Field field = relation.getTo().getField();
            final ClassIndex classIndex = ClassIndex.newInstance(field.getDeclaringClass(), classIndexes.get(field.getDeclaringClass()));
            if (!classIndexObjectMap.containsKey(classIndex)) {
                final FieldValue fieldValue = new FieldValue(field, getFieldValue(from.getField(), object));
                multimap.put(relation.getTo().getInitializationClass(), fieldValue);
            }
        }

        for (Class aClass : multimap.getKeySet()) {
            final Collection<FieldValue> childObject = multimap.get(aClass);
            final Object row = findObject(aClass, childObject);
            classIndexObjectMap.put(ClassIndex.newInstance(aClass, classIndexes.get(aClass)), row);
            loadParentDetails(classIndexes, classIndexObjectMap, aClass, row);
        }
    }

    private Object findObject(final Class<?> type, final Collection<FieldValue> fieldValues) {
        final Map<String, Object> params = new HashMap<String, Object>();

        for (FieldValue fieldValue : fieldValues) {
            params.put(fieldValue.getField().getName(), fieldValue.getValue());
        }

        final List<?> parents = finder.findByAttributes(type, params);

        return parents.isEmpty() ? null : parents.get(0);
    }

    private Object getFieldValue(final Field field, final Object object) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException("Field not accessible: " + field);
        }
    }

    private <T> T findById(final Class<T> type, final Object value) {
        return entityManager.find(type, value);
    }


    private class FieldValue {
        private Field field;
        private Object value;

        FieldValue(final Field field, final Object value) {
            this.field = field;
            this.value = value;
        }

        public Field getField() {
            return field;
        }

        public Object getValue() {
            return value;
        }
    }
}
