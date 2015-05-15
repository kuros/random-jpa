package com.kuro.random.jpa.persistor;

import com.kuro.random.jpa.mapper.FieldValue;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.mapper.TableNode;
import com.kuro.random.jpa.persistor.model.ResultMap;
import com.kuro.random.jpa.persistor.random.Randomize;
import com.kuro.random.jpa.persistor.random.RandomizeImpl;
import com.kuro.random.jpa.types.CreationPlan;
import com.openpojo.random.RandomFactory;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/13/15.
 */
public final class PersistorImpl implements Persistor {

    private EntityManager entityManager;
    private RandomFactory randomFactory;
    private Randomize randomize;

    private PersistorImpl(final EntityManager entityManager, final RandomFactory randomFactory) {
        this.entityManager = entityManager;
        this.randomFactory = randomFactory;
        this.randomize = RandomizeImpl.newInstance(randomFactory);
    }

    public static Persistor newInstance(final EntityManager entityManager, final RandomFactory randomFactory) {
        return new PersistorImpl(entityManager, randomFactory);
    }

//    public ResultMap persist(final CreationPlan creationPlan) {
//        final ResultMap resultMap = ResultMap.newInstance();
//        final List<TableNode> plan = creationPlan.getCreationPlan();
//        for (TableNode tableNode : plan) {
//            final Class tableClass = tableNode.getParentClasses();
//            final Object random = randomize.createRandom(tableClass);
//
//            final List<Relation> relations = tableNode.getRelations();
//
//            for (Relation relation : relations) {
//                createRelation(resultMap, relation, random);
//            }
//
//            resultMap.put(tableClass, random);
//        }
//        return resultMap;
//    }

    private <F, T> void createRelation(final ResultMap resultMap, final Relation<F, T> relation, final Object object) {
        try {
            final Object value = getFieldValue(resultMap, relation.getTo());
            setFieldValue(object, relation.getFrom(), value);
        } catch (Exception e) {
            System.out.println(relation.getFrom().getField() + " -- " + relation.getTo().getField());
            e.printStackTrace();
        }

    }

    private <F> void setFieldValue(final Object object, final FieldValue<F> fieldValue, final Object value) {
        try {

            final Class<?> type = fieldValue.getField().getType();
            fieldValue.getField().setAccessible(true);
            fieldValue.getField().set(object, castNumber(type, value));
        } catch (final IllegalAccessException e) {
        }
    }

    private Object castNumber(final Class<?> type, final Object value) {
        Object returnValue = value;
        if (type.equals(value.getClass())) {
            return returnValue;
        }

        if (value instanceof Number) {
            final  Number number = (Number) value;
            if (Integer.TYPE == type || Integer.class == type) {
                returnValue = number.intValue();
            } else if (Long.TYPE == type || Long.class == type) {
                returnValue = number.longValue();
            } else if (Short.TYPE == type || Short.class == type) {
                returnValue = number.shortValue();
            } else if (Float.TYPE == type || Float.class == type) {
                returnValue = number.floatValue();
            } else if (Double.TYPE == type || Double.class == type) {
                returnValue = number.doubleValue();
            } else if (Byte.TYPE == type || Byte.class == type) {
                returnValue = number.byteValue();
            }
        }

        return returnValue;
    }

    private <T> Object getFieldValue(final ResultMap resultMap, final FieldValue<T> fieldValue) {
        final Object object = resultMap.get(fieldValue.getField().getDeclaringClass());
        Object value = null;
        try {
            fieldValue.getField().setAccessible(true);
            value = fieldValue.getField().get(object);
        } catch (final IllegalAccessException e) {
        }
        return value;
    }


    public ResultMap persist(final CreationPlan creationPlan) {
        return null;
    }
}
