package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.persistor.hepler.Finder;
import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.random.generator.RandomFactory;
import com.github.kuros.random.jpa.types.ClassIndex;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.CreationPlanImpl;
import com.github.kuros.random.jpa.types.FieldIndexValue;
import com.github.kuros.random.jpa.util.ArrayListMultimap;
import com.github.kuros.random.jpa.util.Multimap;
import com.github.kuros.random.jpa.util.NodeHelper;
import com.github.kuros.random.jpa.util.NumberUtil;
import com.github.kuros.random.jpa.util.Util;
import jakarta.persistence.EntityManager;

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
    private final Finder finder;

    public PersistedEntityResolverImpl(final Cache cache) {
        this.entityManager = cache.getEntityManager();
        this.attributeProvider = cache.getAttributeProvider();
        this.hierarchyGraph = cache.getHierarchyGraph();
        this.finder = new Finder(cache);
    }

    public Map<ClassIndex, Object> loadPersistedObjectByIds(final CreationPlan creationPlan) {

        final Map<ClassIndex, Object> classIndexObjectMap = new HashMap<>();
        final CreationPlanImpl plan = (CreationPlanImpl) creationPlan;
        final List<FieldIndexValue> fieldIndexValues = plan.getFieldIndexValues();

        for (FieldIndexValue fieldIndexValue : fieldIndexValues) {
            populateRandomizer(plan, fieldIndexValue);

            if (shouldGenerateParents(fieldIndexValue.getField())) {
                final Field field = fieldIndexValue.getField();
                final Map<Class, Integer> classIndexes = getClassIndicesMap(plan, fieldIndexValue, field);

                final Object value = fieldIndexValue.getValue();
                if (value != null) {
                    final EntityTableMapping entityTableMapping = attributeProvider.get(field.getDeclaringClass());
                    if (entityTableMapping != null && entityTableMapping.getAttributeIds().contains(field.getName())) {
                        final Object transformedValue = NumberUtil.castNumber(fieldIndexValue.getField().getType(), value);
                        final Object byId = findById(fieldIndexValue.getField().getDeclaringClass(), transformedValue);
                        if (byId == null) {
                            throw new IllegalArgumentException("Element not found with id: " + value
                                    + ", Class: " + field.getDeclaringClass());
                        }

                        classIndexObjectMap.put(ClassIndex.newInstance(field.getDeclaringClass(), fieldIndexValue.getIndex()), byId);
                        loadParentDetails(classIndexes, classIndexObjectMap, field.getDeclaringClass(), byId);
                    } else {
                        RandomFactory factory = new RandomFactory();
                        final Object obj = factory.generateRandom(field.getDeclaringClass());
                        final Object transformedValue = NumberUtil.castNumber(field.getType(), value);
                        Util.setFieldValue(field, obj, transformedValue);
                        loadParentDetails(classIndexes, classIndexObjectMap, field.getDeclaringClass(), obj);
                    }
                }
            }

        }
        return classIndexObjectMap;
    }

    private boolean shouldGenerateParents(final Field field) {
        return hierarchyGraph.getAttributeRelations(field.getDeclaringClass()) != null;
    }

    private void populateRandomizer(final CreationPlanImpl plan, final FieldIndexValue fieldIndexValue) {
        final Randomize randomize = plan.getRandomize();

        if (fieldIndexValue.getIndex() == PersistedEntityResolver.DEFAULT_INDEX) {
            randomize.addDefaultFieldValue(fieldIndexValue.getField(), fieldIndexValue.getValue());
        } else {
            randomize.addCustomFieldValue(fieldIndexValue.getField(), fieldIndexValue.getIndex(), fieldIndexValue.getValue());
        }
    }

    private Map<Class, Integer> getClassIndicesMap(final CreationPlanImpl plan, final FieldIndexValue fieldIndexValue, final Field field) {

        final int index = fieldIndexValue.getIndex() == PersistedEntityResolver.DEFAULT_INDEX ? 0 : fieldIndexValue.getIndex();
        final List<ClassIndex> classIndexInOrder = NodeHelper.getClassIndexInOrder(plan.getRoot(),
                field.getDeclaringClass(),
                index);

        final Map<Class, Integer> classIndexMap = new HashMap<>();
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
                final FieldValue fieldValue = new FieldValue(field, NumberUtil.castNumber(field.getType(), getFieldValue(from.getField(), object)));
                multimap.put(relation.getTo().getInitializationClass(), fieldValue);
            }
        }

        for (Class aClass : multimap.getKeySet()) {
            final Collection<FieldValue> childObject = multimap.get(aClass);
            final Object row = findObject(aClass, childObject);
            if (row != null) {
                classIndexObjectMap.put(ClassIndex.newInstance(aClass, classIndexes.get(aClass)), row);
                loadParentDetails(classIndexes, classIndexObjectMap, aClass, row);
            }
        }
    }

    private Object findObject(final Class<?> type, final Collection<FieldValue> fieldValues) {
        final Map<String, Object> params = new HashMap<>();

        for (FieldValue fieldValue : fieldValues) {
            final Object afterCast = NumberUtil.castNumber(fieldValue.getField().getType(), fieldValue.getValue());
            params.put(fieldValue.getField().getName(), afterCast);
        }

        final List<?> parents = finder.findByAttributes(type, params);
        return parents.isEmpty() ? null : parents.get(0);
    }

    private Object getFieldValue(final Field field, final Object object) {
        return Util.getFieldValue(object, field);
    }

    private <T> T findById(final Class<T> type, final Object value) {
        return entityManager.find(type, value);
    }


    private class FieldValue {
        final private Field field;
        final private Object value;

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
