package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.metamodel.annotation.AnnotatedMetaModelProvider;
import com.github.kuros.random.jpa.testUtil.MockEntityManagerProvider;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public class AnnotatedMetaModelProviderTest {

    private MockEntityManagerProvider entityManagerProvider;
    private EntityManager entityManager;
    private MetaModelProvider metaModelProvider;

    @Before
    public void setUp() throws Exception {
        entityManagerProvider = MockEntityManagerProvider.createMockEntityManager();
        entityManager = entityManagerProvider.getEntityManager();
        metaModelProvider = new AnnotatedMetaModelProvider(entityManager);
    }

    @Test
    public void mapMetaModelsToTheirTableNames() {
        final Map<String, List<FieldName>> result = metaModelProvider.getFieldsByTableName();

        final List<FieldName> fieldNames = result.get("Employee");
        assertEquals(3, fieldNames.size());
        validate("employee_id", fieldNames.get(0).getOverridenFieldName());
        validate("person_id", fieldNames.get(1).getOverridenFieldName());
        validate("salary", fieldNames.get(2).getFieldName());
        validate("", fieldNames.get(2).getOverridenFieldName());
    }

    private void validate(final String expected, final String actual) {
        assertEquals(expected, actual);
    }
}
