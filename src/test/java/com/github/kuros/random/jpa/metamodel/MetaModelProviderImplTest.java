package com.github.kuros.random.jpa.metamodel;

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
public class MetaModelProviderImplTest {

    private MockEntityManagerProvider entityManagerProvider;
    private EntityManager entityManager;
    private MetaModelProvider metaModelProvider;

    @Before
    public void setUp() throws Exception {
        entityManagerProvider = MockEntityManagerProvider.createMockEntityManager();
        entityManager = entityManagerProvider.getEntityManager();
        metaModelProvider = new MetaModelProviderImpl(entityManager);
    }

    @Test
    public void mapMetaModelsToTheirTableNames() {
        final Map<String, List<FieldName>> result = metaModelProvider.getFieldsByTableName();

        final List<FieldName> fieldNames = result.get("Employee");
        assertEquals(3, fieldNames.size());
        validate("employee_id", fieldNames.get(0).getOverriddenFieldName());
        validate("person_id", fieldNames.get(1).getOverriddenFieldName());
        validate("salary", fieldNames.get(2).getFieldName());
        validate("", fieldNames.get(2).getOverriddenFieldName());
    }

    private void validate(final String expected, final String actual) {
        assertEquals(expected, actual);
    }
}
