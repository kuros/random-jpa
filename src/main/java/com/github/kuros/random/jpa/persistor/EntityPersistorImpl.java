package com.github.kuros.random.jpa.persistor;

import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.definition.HierarchyGraph;
import com.github.kuros.random.jpa.definition.TableNode;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.persistor.functions.FunctionProcessor;
import com.github.kuros.random.jpa.persistor.model.ResultNodeTree;
import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.resolver.PersistedEntityResolver;
import com.github.kuros.random.jpa.resolver.PersistedEntityResolverImpl;
import com.github.kuros.random.jpa.types.ClassIndex;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.CreationPlanImpl;
import com.github.kuros.random.jpa.types.Node;
import com.github.kuros.random.jpa.types.ResultNode;
import com.github.kuros.random.jpa.util.NumberUtil;
import com.github.kuros.random.jpa.util.Util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public final class EntityPersistorImpl implements Persistor {

    private final Cache cache;
    private final Randomize randomize;
    private final FunctionProcessor functionProcessor;
    private final HierarchyGraph hierarchyGraph;

    private EntityPersistorImpl(final Cache cache, final HierarchyGraph hierarchyGraph, final Randomize randomize) {
        this.cache = cache;
        this.functionProcessor = new FunctionProcessor(cache);
        this.randomize = randomize;
        this.hierarchyGraph = hierarchyGraph;
    }

    public static Persistor newInstance(final Cache cache, final HierarchyGraph hierarchyGraph, final Randomize randomize) {
        return new EntityPersistorImpl(cache, hierarchyGraph, randomize);
    }

    @SuppressWarnings("unchecked")
    public ResultNodeTree persist(final CreationPlan creationPlan) {
        final ResultNode root = ResultNode.newInstance();
        final ResultNodeTree resultNodeTree = ResultNodeTree.newInstance(cache, root);

        final Node creationPlanRoot = ((CreationPlanImpl) creationPlan).getRoot();
        final List<Node> childNodes = creationPlanRoot.getChildNodes();

        final Map<ClassIndex, Object> classIndexMap = getClassIndexObjectMap(creationPlan);

        persist(classIndexMap, root, resultNodeTree, childNodes);

        cache.getEntityManager().flush();
        return resultNodeTree;
    }

    private Map<ClassIndex, Object> getClassIndexObjectMap(final CreationPlan creationPlan) {
        final PersistedEntityResolver persistedEntityResolver = new PersistedEntityResolverImpl(cache);
        return persistedEntityResolver.loadPersistedObjectByIds(creationPlan);
    }

    private void persist(final Map<ClassIndex, Object> classIndexMap, final ResultNode resultNode, final ResultNodeTree resultNodeTree, final List<Node> childNodes) {
        for (Node childNode : childNodes) {
            if (childNode.getValue() != null) {
                final ResultNode resultChildNode = ResultNode.newInstance(childNode.getType(), getIndex(resultNodeTree, childNode.getType()));
                resultNode.addChildNode(resultChildNode);
                persist(classIndexMap, resultChildNode, resultNodeTree, childNode);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void persist(final Map<ClassIndex, Object> classIndexMap, final ResultNode resultNode, final ResultNodeTree resultNodeTree, final Node node) {

        Object persistedObject = classIndexMap.get(ClassIndex.newInstance(node.getType(), node.getIndex()));

        if (persistedObject == null) {
            persistedObject = classIndexMap.get(ClassIndex.newInstance(node.getType(), PersistedEntityResolver.DEFAULT_INDEX));
            if (persistedObject == null) {
                final Object random = createRandomObject(node, resultNodeTree);
                persistedObject = functionProcessor.findOrSave(random);
            }
        }

        resultNode.setValue(persistedObject);
        resultNodeTree.put(node.getType(), persistedObject);

        final List<Node> childNodes = node.getChildNodes();
        persist(classIndexMap, resultNode, resultNodeTree, childNodes);

    }

    private int getIndex(final ResultNodeTree resultNodeTree, final Class type) {
        final List<Object> objects = resultNodeTree.getCreatedEntities().get(type);
        return isEmpty(objects) ? 0 : objects.size();
    }

    private boolean isEmpty(final List<Object> objects) {
        return objects == null || objects.isEmpty();
    }

    private Object createRandomObject(final Node node, final ResultNodeTree resultNodeTree) {
        final Object random = node.getValue();

        final TableNode tableNode = getTableNode(node.getType());
        if (tableNode != null) {
            final List<Relation> relations = tableNode.getRelations();

            for (Relation relation : relations) {
                createRelation(resultNodeTree, relation);
            }
        }

        return randomize.populateRandomFields(random, getIndexForNewEntity(resultNodeTree, random.getClass()));
    }

    private TableNode getTableNode(final Class<?> type) {
        return hierarchyGraph.getParentRelations().get(type);
    }

    private void createRelation(final ResultNodeTree resultNodeTree, final Relation relation) {
        try {
            final Field fromField = relation.getFrom().getField();
            if (!randomize.isValueProvided(fromField, getIndexForNewEntity(resultNodeTree, fromField.getDeclaringClass()))) {
                final Object value = getFieldValue(resultNodeTree, relation);
                randomize.addFieldValue(fromField, getIndexForNewEntity(resultNodeTree, fromField.getDeclaringClass()), value);
            }
        } catch (final Exception e) {
            //do nothing
        }

    }

    private Object getFieldValue(final ResultNodeTree resultNodeTree, final Relation relation) {
        final Field field = relation.getTo().getField();
        final List<Object> objects = getPersistedEntitiesByFieldDeclaringClass(resultNodeTree, field.getDeclaringClass());
        final Object object = objects.get(objects.size() - 1);
        final Object value;
        final Field fromField = relation.getFrom().getField();
        if (fromField.getType().equals(object.getClass())) {
            value = object;
        } else {
            value = NumberUtil.castNumber(fromField.getType(), Util.getFieldValue(object, field));
        }
        return value;
    }

    private int getIndexForNewEntity(final ResultNodeTree resultNodeTree, final Class<?> declaringClass) {
        final List<Object> values = getPersistedEntitiesByFieldDeclaringClass(resultNodeTree, declaringClass);
        return values != null ? values.size() : 0;
    }

    private List<Object> getPersistedEntitiesByFieldDeclaringClass(final ResultNodeTree resultNodeTree, final Class<?> declaringClass) {
        final Map<Class<?>, List<Object>> createdEntities = resultNodeTree.getCreatedEntities();
        return createdEntities.get(declaringClass);
    }
}
