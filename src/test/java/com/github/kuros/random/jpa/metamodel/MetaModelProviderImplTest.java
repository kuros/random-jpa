package com.github.kuros.random.jpa.metamodel;

import com.github.kuros.random.jpa.Database;
import com.github.kuros.random.jpa.cache.Cache;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.R;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
public class MetaModelProviderImplTest {

    private EntityManager entityManager;
    private MetaModelProvider metaModelProvider;
    private Cache cache;

    @Before
    public void setUp() throws Exception {
        entityManager = EntityManagerProvider.getEntityManager();
        cache = Cache.create(Database.NONE, entityManager);
        metaModelProvider = new MetaModelProviderImpl(cache);
    }

    @Test
    public void mapMetaModelsToTheirTableNames() {
        final Map<String, List<FieldWrapper>> result = metaModelProvider.getFieldsByTableName();

        final List<FieldWrapper> fieldWrappers = result.get("r");
        assertEquals(2, fieldWrappers.size());
        validate("id", fieldWrappers.get(0).getFieldName());
        validate("id", fieldWrappers.get(0).getOverriddenFieldName());
        validate("pId", fieldWrappers.get(1).getFieldName());
        validate("p_id", fieldWrappers.get(1).getOverriddenFieldName());
        assertEquals(R.class, fieldWrappers.get(0).getInitializationClass());
        assertEquals(R.class, fieldWrappers.get(1).getInitializationClass());
    }

    private void validate(final String expected, final String actual) {
        assertEquals(expected, actual);
    }
}
