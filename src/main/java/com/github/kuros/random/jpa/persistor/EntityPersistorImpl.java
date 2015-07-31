package com.github.kuros.random.jpa.persistor;

import com.github.kuros.random.jpa.definition.TableNode;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.persistor.functions.FunctionProcessor;
import com.github.kuros.random.jpa.persistor.model.ResultMap;
import com.github.kuros.random.jpa.persistor.model.ResultMapImpl;
import com.github.kuros.random.jpa.random.Randomize;
import com.github.kuros.random.jpa.types.CreationOrder;
import com.github.kuros.random.jpa.types.CreationPlan;
import com.github.kuros.random.jpa.types.Node;
import com.github.kuros.random.jpa.types.ResultNode;
import com.github.kuros.random.jpa.util.NumberUtil;

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

    private final Randomize randomize;

    private EntityPersistorImpl(final Randomize randomize) {
        this.randomize = randomize;
    }

    public static Persistor newInstance(final Randomize randomize) {
        return new EntityPersistorImpl(randomize);
    }

    @SuppressWarnings("unchecked")
    public ResultMap persist(final CreationPlan creationPlan) {
        final ResultNode root = ResultNode.newInstance();
        final ResultMapImpl resultMap = ResultMapImpl.newInstance(root);


        final Node creationPlanRoot = creationPlan.getRoot();
        final List<Node> childNodes = creationPlanRoot.getChildNodes();
        for (Node node : childNodes) {
            if (node.getValue() != null) {
                final ResultNode childNode = ResultNode.newInstance(node.getType(), getIndex(resultMap, node.getType()));
                root.addChildNode(childNode);
                persist(childNode, creationPlan.getCreationOrder(), resultMap, node);
            }
        }

        return resultMap;
    }

    private int getIndex(final ResultMapImpl resultMap, final Class type) {
        final List<Object> objects = resultMap.getCreatedEntities().get(type);
        return isEmpty(objects) ? 0 : objects.size();
    }

    private boolean isEmpty(final List<Object> objects) {
        return objects == null || objects.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private void persist(final ResultNode resultNode, final CreationOrder creationOrder, final ResultMapImpl resultMap, final Node node) {
        final Object random = createRandomObject(node, creationOrder, resultMap);
        final Object persistedObject = FunctionProcessor.findOrSave(random);

        resultNode.setValue(persistedObject);
        resultMap.put(node.getType(), persistedObject);

        final List<Node> childNodes = node.getChildNodes();
        for (Node childNode : childNodes) {
            if (childNode.getValue() != null) {
                final ResultNode resultChildNode = ResultNode.newInstance(childNode.getType(), getIndex(resultMap, childNode.getType()));
                resultNode.addChildNode(resultChildNode);
                persist(resultChildNode, creationOrder, resultMap, childNode);
            }
        }

    }

    private Object createRandomObject(final Node node, final CreationOrder creationOrder, final ResultMapImpl resultMap) {
        final Object random = node.getValue();

        final TableNode tableNode = creationOrder.getTableNode(node.getType());
        if (tableNode != null) {
            final List<Relation> relations = tableNode.getRelations();

            for (Relation relation : relations) {
                createRelation(resultMap, relation, random);
            }
        }

        return randomize.populateRandomFields(random);
    }

    private void createRelation(final ResultMapImpl resultMap, final Relation relation, final Object object) {
        try {
            if (!randomize.isValueProvided(relation.getFrom().getField())) {
                final Object value = getFieldValue(resultMap, relation.getTo().getField());
                setFieldValue(object, relation.getFrom().getField(), value);
            }
        } catch (final Exception e) {
            //do nothing
        }

    }

    private void setFieldValue(final Object object, final Field field, final Object value) {
        try {

            final Class<?> type = field.getType();
            field.setAccessible(true);
            field.set(object, NumberUtil.castNumber(type, value));
        } catch (final IllegalAccessException e) {
            //do nothing
        }
    }

    private Object getFieldValue(final ResultMapImpl resultMap, final Field field) {
        final Map<Class<?>, List<Object>> createdEntities = resultMap.getCreatedEntities();
        final List<Object> objects = createdEntities.get(field.getDeclaringClass());
        final Object object = objects.get(objects.size() - 1);
        Object value = null;
        try {
            field.setAccessible(true);
            value = field.get(object);
        } catch (final IllegalAccessException e) {
            //do nothing
        }
        return value;
    }
}
