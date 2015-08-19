package com.github.kuros.random.jpa.provider.oracle;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.base.AbstractCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.model.ColumnCharacterLength;
import com.github.kuros.random.jpa.provider.model.ColumnDetail;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.HashMap;
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
public final class OracleCharacterLengthProvider extends AbstractCharacterLengthProvider {

    private static final String QUERY = "SELECT" +
            " dt.table_name, dt.column_name, dt.char_col_decl_length, dt.DATA_PRECISION, dt.DATA_SCALE" +
            " FROM user_tab_columns dt";

    @VisibleForTesting
    OracleCharacterLengthProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(attributeProvider, entityManager);
    }

    public static OracleCharacterLengthProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new OracleCharacterLengthProvider(entityManager, attributeProvider);
    }

    @Override
    public String getQuery() {
        return QUERY;
    }

    protected Map<String, ColumnCharacterLength> init() {
        final Map<String, ColumnCharacterLength> lengths = new HashMap<String, ColumnCharacterLength>();
        final Query query = entityManager.createNativeQuery(getQuery());
        final List resultList = query.getResultList();
        for (Object o : resultList) {
            final Object[] row = (Object[]) o;

            final EntityTableMapping entityTableMapping = attributeProvider.get((String) row[0]);

            if (entityTableMapping == null) {
                continue;
            }

            final String attributeName = entityTableMapping.getAttributeName((String) row[1]);
            final BigDecimal length = (BigDecimal) row[2];
            final BigDecimal precision = (BigDecimal) row[3];
            final BigDecimal scale = (BigDecimal) row[4];

            final String entityName = entityTableMapping.getEntityName();
            ColumnCharacterLength columnCharacterLength = lengths.get(entityName);
            if (columnCharacterLength == null) {
                columnCharacterLength = ColumnCharacterLength.newInstance();
                lengths.put(entityName, columnCharacterLength);
            }

            columnCharacterLength.add(attributeName, new ColumnDetail(getValue(length), getValue(precision), getValue(scale)));
        }

        return lengths;
    }

    private Integer getValue(final BigDecimal bigDecimal) {
        return bigDecimal == null ? null : bigDecimal.intValue();
    }
}
