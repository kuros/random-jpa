package com.kuro.random.jpa.persistor;

import com.kuro.random.jpa.mapper.FieldValue;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.mapper.TableNode;
import com.kuro.random.jpa.persistor.model.ResultMap;
import com.kuro.random.jpa.persistor.random.Randomize;
import com.kuro.random.jpa.persistor.random.RandomizeImpl;
import com.kuro.random.jpa.persistor.random.generator.RandomGenerator;
import com.kuro.random.jpa.types.CreationOrder;
import com.kuro.random.jpa.util.NumberUtil;

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
    private Randomize randomize;

    private PersistorImpl(final EntityManager entityManager, final RandomGenerator randomGenerator) {
        this.entityManager = entityManager;
        this.randomize = RandomizeImpl.newInstance(randomGenerator);
    }

    public static Persistor newInstance(final EntityManager entityManager, final RandomGenerator randomGenerator) {
        return new PersistorImpl(entityManager, randomGenerator);
    }

    public ResultMap persist(final CreationOrder creationOrder) {
        final ResultMap resultMap = ResultMap.newInstance();
        final List<Class<?>> plan = creationOrder.getCreationPlan();
        for (Class tableClass : plan) {
            final Object random = createRandomObject(tableClass, creationOrder, resultMap);

            Object persistedObject;
            if (getId(tableClass, random) != null
                    && findElementById(tableClass, random) != null) {
                persistedObject = findElementById(tableClass, random);
            } else {
                persistedObject = persistAndReturnPersistedObject(tableClass, random);
            }

            resultMap.put(tableClass, persistedObject);
        }
        return resultMap;
    }

    private Object persistAndReturnPersistedObject(final Class tableClass, final Object random) {
        final EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
        final EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(random);
        em.getTransaction().commit();
        em.close();
        return findElementById(tableClass, random);
    }

    private Object findElementById(final Class tableClass, final Object persistedObject) {
        return entityManager.find(tableClass, getId(tableClass, persistedObject));
    }

    private Object getId(final Class tableClass, final Object persistedObject) {
        final Field[] declaredFields = tableClass.getDeclaredFields();
        Field field = null;
        for (Field declaredField : declaredFields) {
            if (declaredField.getAnnotation(Id.class) != null) {
                field = declaredField;
                field.setAccessible(true);
                break;
            }
        }

        Object id = null;
        try {
            id = field.get(persistedObject);
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        return id;
    }


    private Object createRandomObject(final Class tableClass, final CreationOrder creationOrder, final ResultMap resultMap) {
        final Object random = randomize.createRandom(tableClass);

        final TableNode tableNode = creationOrder.getTableNode(tableClass);
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
            e.printStackTrace();
        }

    }

    private <F> void setFieldValue(final Object object, final FieldValue<F> fieldValue, final Object value) {
        try {

            final Class<?> type = fieldValue.getField().getType();
            fieldValue.getField().setAccessible(true);
            fieldValue.getField().set(object, NumberUtil.castNumber(type, value));
        } catch (final IllegalAccessException e) {
        }
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
