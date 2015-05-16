package com.kuro.random.jpa.persistor;

import com.kuro.random.jpa.mapper.FieldValue;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.mapper.TableNode;
import com.kuro.random.jpa.persistor.model.ResultMap;
import com.kuro.random.jpa.persistor.random.Randomize;
import com.kuro.random.jpa.persistor.random.RandomizeImpl;
import com.kuro.random.jpa.persistor.random.generator.RandomGenerator;
import com.kuro.random.jpa.types.CreationPlan;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/13/15.
 */
public final class PersistorImpl implements Persistor {

    private EntityManager entityManager;
    private RandomGenerator randomGenerator;
    private Randomize randomize;

    private PersistorImpl(final EntityManager entityManager, final RandomGenerator randomGenerator) {
        this.entityManager = entityManager;
        this.randomGenerator = randomGenerator;
        this.randomize = RandomizeImpl.newInstance(randomGenerator);
    }

    public static Persistor newInstance(final EntityManager entityManager, final RandomGenerator randomGenerator) {
        return new PersistorImpl(entityManager, randomGenerator);
    }

    public ResultMap persist(final CreationPlan creationPlan) {
        final ResultMap resultMap = ResultMap.newInstance();
        final List<Class<?>> plan = creationPlan.getCreationPlan();
        for (Class tableClass : plan) {
            final Object random = createRandomObject(tableClass, creationPlan, resultMap);
            final EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
            final EntityManager em = entityManagerFactory.createEntityManager();
            em.getTransaction().begin();
            em.persist(random);
            em.getTransaction().commit();
            em.close();
            final Object persistedObject = findElementById(tableClass, random);
            resultMap.put(tableClass, persistedObject);
        }
        return resultMap;
    }

    private Object findElementById(final Class tableClass, final Object persistedObject) {
        final Field[] declaredFields = tableClass.getDeclaredFields();
        Field field = null;
        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(Id.class) != null) {
                field = declaredField;
                break;
            }
        }

        field.setAccessible(true);
        Object id = null;
        try {
            id = field.get(persistedObject);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }

        return entityManager.find(tableClass, id);
    }


    private Object createRandomObject(final Class tableClass, final CreationPlan creationPlan, final ResultMap resultMap) {
        final Object random = randomize.createRandom(tableClass);

        final TableNode tableNode = creationPlan.getTableNode(tableClass);
        final List<Relation<?, ?>> relations = tableNode.getRelations();

        for (Relation relation : relations) {
            createRelation(resultMap, relation, random);
        }


        return random;
    }

    private <F, T> void createRelation(final ResultMap resultMap, final Relation<F, T> relation, final Object object) {
        try {
            final Object value = getFieldValue(resultMap, relation.getTo());
            setFieldValue(object, relation.getFrom(), value);
        } catch (final Exception e) {
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
        if (value == null || type.equals(value.getClass())) {
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
}
