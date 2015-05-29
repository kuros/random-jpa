package com.github.kuros.random.jpa.persistor;

import com.github.kuros.random.jpa.mapper.ProcessingType;
import com.github.kuros.random.jpa.persistor.annotation.AnnotatedEntityPersistor;

import javax.persistence.EntityManager;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public class PersistorFactory {

    public static Persistor getPersistor(final ProcessingType processingType, final EntityManager entityManager) {
        switch (processingType) {
            case ANNOTATION:
                return AnnotatedEntityPersistor.newInstance(entityManager);

            default:
                throw new UnsupportedOperationException("processing type should match to Annotation/XML");
        }
    }
}
