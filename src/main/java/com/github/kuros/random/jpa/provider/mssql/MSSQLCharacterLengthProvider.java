package com.github.kuros.random.jpa.provider.mssql;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.base.AbstractCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.model.ColumnCharacterLength;
import com.github.kuros.random.jpa.provider.model.ColumnDetail;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
public final class MSSQLCharacterLengthProvider extends AbstractCharacterLengthProvider {

    private static final String QUERY = "SELECT isc.TABLE_NAME,\n" +
            "  COLUMN_NAME,\n" +
            "  CHARACTER_MAXIMUM_LENGTH,\n" +
            "  isc.NUMERIC_PRECISION,\n" +
            "  isc.NUMERIC_SCALE,\n" +
            "  isc.DATA_TYPE\n" +
            "FROM INFORMATION_SCHEMA.COLUMNS isc\n" +
            "  INNER JOIN information_schema.tables ist\n" +
            "    ON isc.table_name = ist.table_name\n" +
            "WHERE Table_Type = 'BASE TABLE'\n";

    @VisibleForTesting
    MSSQLCharacterLengthProvider(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        super(attributeProvider, entityManager);
    }

    public static MSSQLCharacterLengthProvider getInstance(final EntityManager entityManager, final AttributeProvider attributeProvider) {
        return new MSSQLCharacterLengthProvider(entityManager,
                attributeProvider);
    }

    protected Map<String, ColumnCharacterLength> init() {
        final Map<String, ColumnCharacterLength> lengths = new HashMap<String, ColumnCharacterLength>();
        final Query query = getEntityManager().createNativeQuery(getQuery());
        final List resultList = query.getResultList();
        for (Object o : resultList) {
            final Object[] row = (Object[]) o;

            final EntityTableMapping entityTableMapping = getAttributeProvider().get((String) row[0]);

            if (entityTableMapping == null) {
                continue;
            }

            final String attributeName = entityTableMapping.getAttributeName((String) row[1]);
            final Integer length = (Integer) row[2];
            final Byte precision = (Byte) row[3];
            final Integer scale = (Integer) row[4];
            final String dataType = (String) row[5];

            final ColumnDetail columnDetail = new ColumnDetail(getValue(length), getValue(precision), getValue(scale), DATA_TYPE_MAP.get(dataType.toLowerCase()));

            final String entityName = entityTableMapping.getEntityName();
            ColumnCharacterLength columnCharacterLength = lengths.get(entityName);
            if (columnCharacterLength == null) {
                columnCharacterLength = ColumnCharacterLength.newInstance();
                lengths.put(entityName, columnCharacterLength);
            }

            columnCharacterLength.add(attributeName, columnDetail);
        }

        return lengths;
    }

    @Override
    public String getQuery() {
        return QUERY;
    }

    private Integer getValue(final Number number) {
        return number == null ? null : number.intValue();
    }

}
